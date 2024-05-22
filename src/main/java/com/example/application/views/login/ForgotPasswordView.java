package com.example.application.views.login;

import com.example.application.data.PasswordResetToken;
import com.example.application.data.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;
import static com.vaadin.flow.data.binder.ValidationResult.error;
import static com.vaadin.flow.data.binder.ValidationResult.ok;

@Route(value = "forgot")
@PageTitle("Password reset")
@AnonymousAllowed
public class ForgotPasswordView extends VerticalLayout implements HasUrlParameter<String>, AfterNavigationObserver {
    @Autowired
    private final UserService userService;

    private String url;

    private final FormLayout passwordForm;

    private final BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    private PasswordField password;
    private PasswordField passwordConfirm;

    private Button submitButton;

    public ForgotPasswordView(UserService userService){
        this.userService = userService;


        final H2 registerHeader = new H2("Password reset");
        this.passwordForm = new FormLayout();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(registerHeader,passwordForm);
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        url = parameter;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
//        if(url.isBlank()) {
//            UI.getCurrent().navigate("login");
//        }else
        if(url.equalsIgnoreCase("success")) {
            H3 title1 = new H3("A password recovery link was sent. Check your mailbox! \n  It may have ended up in spam folder");
            passwordForm.add(title1);
        }else {
            PasswordResetToken token = userService.validatePasswordToken(url);
            if(token != null){
                initFormLayout();
                initBinder();
            }

        }
    }


    private void initFormLayout(){
        password = new PasswordField("New password");
        password.setRequired(true);

        passwordConfirm = new PasswordField("Confirm new password");
        passwordConfirm.setRequired(true);
        passwordConfirm.addValueChangeListener(
                e -> {
                    passwordConfirm.setInvalid(false);
                    password.setInvalid(false);
                    binder.validate();
                }
        );
        submitButton =new Button("Submit");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.addClickListener(
                e -> {
                    try {
                        final User userBean = userService.getUserByPasswordResetToken(url);
                        binder.writeBean(userBean);

                        userService.changePassword(userBean);
                        showSuccess();
                    }catch (final ValidationException validationException){
                        return;
                    }
                }
        );
        passwordForm.add(
                password,
                passwordConfirm,
                submitButton
        );

        passwordForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, TOP));

        passwordForm.setMaxWidth("400px");
        passwordForm.getStyle().set("margin", "0 auto");
    }


    private void initBinder(){
        binder.forField(password)
                .withValidator(this::passwordValidator).bind("password");
        binder.forField(passwordConfirm)
                .withValidator(
                        val -> val.equals(password.getValue()),
                        "Confirmed password doesn't match initial password");
    }

    private ValidationResult passwordValidator(final String initialPassword, final ValueContext context){
        if(initialPassword == null || initialPassword.length() < 8){
            passwordConfirm.setInvalid(true);
            passwordConfirm.setErrorMessage("Password should be at least 8 characters long");
            return error("Password should be at least 8 characters long");
        }
        if(initialPassword.equals(passwordConfirm.getValue())){
            passwordConfirm.setInvalid(false);
            return ok();
        }else {
            final String errorMessage = "Confirmed password doesn't match initial password";
            passwordConfirm.setInvalid(true);
            passwordConfirm.setErrorMessage(errorMessage);
            return error(errorMessage);
        }
    }

    private void showSuccess() {
        Notification notification =
                Notification.show("Password changed, you can login now");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        UI.getCurrent().navigate("login");
    }

}
