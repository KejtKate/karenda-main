package com.example.application.views.login;

import com.example.application.data.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;
import static com.vaadin.flow.data.binder.ValidationResult.error;
import static com.vaadin.flow.data.binder.ValidationResult.ok;

@PageTitle("Register")
@Route(value = "register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    @Autowired
    private final UserService userService;

    private final FormLayout userForm;

    private final BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    private TextField name;
    private TextField username;
    private EmailField email;
    private PasswordField password;
    private PasswordField passwordConfirm;
    private Button submitButton;
    public RegisterView(UserService userService) {
        this.userService = userService;

        final H2 registerHeader = new H2("Karenda registration");
        this.userForm = new FormLayout();

        initFormFields();
        initFormLayoutView();
        initBinder();

        // Center the RegistrationForm
        setHorizontalComponentAlignment(Alignment.CENTER, userForm);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(registerHeader,userForm);

    }

    private void initFormFields(){
        name = new TextField("Name");
        name.setRequired(true);

        username = new TextField("Username");
        username.setRequired(true);

        email = new EmailField("Email");
        email.setRequired(true);
        email.setErrorMessage("Enter a valid email address");

        password = new PasswordField("Password");
        password.setRequired(true);

        passwordConfirm = new PasswordField("Confirm password");
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
                        final User userBean = new User();
                        binder.writeBean(userBean);
                        userService.saveUser(userBean);
                        showSuccess(userBean);
                    }catch (final ValidationException validationException){
                        return;
                    }
                }
        );

        userForm.add(
                name,
                username,
                email,
                password,
                passwordConfirm,
                submitButton
        );
    }

    private void initFormLayoutView(){
        userForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, TOP), new FormLayout.ResponsiveStep("500px", 2, TOP));

        userForm.setColspan(email, 2);
        userForm.setColspan(submitButton, 2);
        userForm.setMaxWidth("650px");
        userForm.getStyle().set("margin", "0 auto");

    }

    private void initBinder(){
        binder.forField(name)
                .withValidator(
                        val ->!val.isBlank(), "Name must not be empty")
                .bind(User::getName, User::setName);

        binder.forField(username)
                .withValidator(this::userValidator).bind(User::getUsername, User::setUsername);
        binder.forField(email)
                .withValidator(new EmailValidator("You should enter a valid email")).bind(User::getEmail, User::setEmail);
        binder.forField(password)
                .withValidator(this::passwordValidator).bind(User::getPassword, User::setPassword);
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

    private ValidationResult userValidator(String username, ValueContext ctx) {

        if (username == null || username.length() < 4) {
            return ValidationResult.error("Username has to be longer than 4");
        }
        if(!userService.usernameExists(username)) {
            return ValidationResult.ok();
        }
        return ValidationResult.error("This username is already taken.");
    }

    private void showSuccess(User userBean) {
        Notification notification =
                Notification.show("Data saved, welcome " + userBean.getName());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        UI ui = UI.getCurrent();
        ui.navigate("login");
    }

}
