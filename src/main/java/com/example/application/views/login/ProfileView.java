package com.example.application.views.login;

import com.example.application.data.User;
import com.example.application.services.UserService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition.TOP;


@PageTitle("Profile")
@Route(value = "profile", layout = MainLayout.class)
@PermitAll
public class ProfileView extends VerticalLayout implements BeforeLeaveObserver {

    @Autowired
    private final UserService userService;

    private final BeanValidationBinder<User> userBinder = new BeanValidationBinder<>(User.class);

    private User user;

    private FormLayout userForm;
    private TextField name;
    private EmailField email;
    private Button applyChangesButton;



    public ProfileView(UserService userService){
        this.userService = userService;
        this.user = VaadinSession.getCurrent().getAttribute(User.class);
        this.userForm = new FormLayout();

        setId("profile-form-layout");

        initFormFields();
        initFormLayouts();
        initBinders();
        populateData();

        setHorizontalComponentAlignment(Alignment.CENTER, userForm);

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(userForm);
    }

    private void initFormFields() {
        name = new TextField("Name");
        email = new EmailField("Email");

        applyChangesButton = new Button("Apply changes");
        applyChangesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        applyChangesButton.addClickListener(e -> {
                    try {
                        userBinder.writeBean(user);
                        userService.update(user);
                    } catch (ValidationException validationException) {}
                });
        userForm.add(
                name,
                email,
                applyChangesButton
        );

    }
    private void initFormLayouts() {
        userForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, TOP), new FormLayout.ResponsiveStep("600", 3, TOP));
        userForm.setId("profile-form");
        userForm.setWidth("700px");
        userForm.getStyle().setPadding("enabled");
        userForm.setColspan(name, 3);
        userForm.setColspan(email, 3);
        userForm.setColspan(applyChangesButton, 3);

    }

    private void initBinders() {
        userBinder
                .forField(name)
                .withValidator(val -> !val.isBlank(), "Name must not be empty")
                .bind(User::getName, User::setName);
        userBinder
                .forField(email)
                .withValidator(new EmailValidator("You should enter valid email"))
                .bind(User::getEmail, User::setEmail);

    }

    private void populateData() {
        userBinder.readBean(user);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (userBinder.hasChanges()) {
            final BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
            final ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setText("You have unsaved changes! Are you sure you want to leave?");
            confirmDialog.setConfirmButton("Stay", e -> confirmDialog.close());
            confirmDialog.setCancelButton("Leave", e -> action.proceed());
            confirmDialog.setCancelable(true);
            confirmDialog.open();
        }
    }

}
