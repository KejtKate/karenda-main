package com.example.application.views.login;

import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.UUID;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    @Autowired
    private final UserService userService;

    private final MailSender mailSender;

    private EmailField emailField;

    public LoginView(AuthenticatedUser authenticatedUser, UserService userService, MailSender mailSender) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.mailSender = mailSender;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Karenda");
        i18n.getHeader().setDescription("Login using user/user or admin/admin");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(e -> onForgetPasswordClick());

        RouterLink registerLink = new RouterLink("Don't have an account yet? Sign up!", RegisterView.class);
        Paragraph registerParagraph = new Paragraph(registerLink);
        registerParagraph.addClassName(LumoUtility.TextAlignment.CENTER);
        getFooter().add(registerParagraph);
        setOpened(true);
    }
    public void onForgetPasswordClick(){
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        dialog.setHeaderTitle("Forgot Password?");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        TextArea information = new TextArea();
        information.setValue("We will send a recovery password link to your email. Click on it to change the password.");
        information.setReadOnly(true);
        information.setMinWidth("300px");
        emailField = new EmailField("Email:");
        VerticalLayout dialogLayout = new VerticalLayout(information,
                emailField);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialog.add(dialogLayout);
        Button okButton = new Button("Send link");

        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
        binder.forField(emailField).withValidator(new EmailValidator("You should enter a valid email")).bind("email");

        okButton.addClickListener(click ->{
            if(!binder.validate().isOk())
                return;
            String mail = emailField.getValue();
            resetPassword(mail);
            dialog.close();

        });
        dialog.getFooter().add(okButton);
        dialog.open();
    }

    public void resetPassword(String userEmail){
        User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            throw new EntityNotFoundException();
        }
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String subject = "Password reset - Karenda app";
        String text = "Click on a link to reset password: \n";
        String link = "http://localhost:8080/forgot/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(text + link);
        message.setTo(userEmail);
        message.setFrom("karendaapp@op.pl");

        mailSender.send(message);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
