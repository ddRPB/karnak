package org.karnak.ui.project;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.StringUtils;
import org.karnak.data.gateway.Project;
import org.karnak.profilepipe.utils.HMAC;
import org.karnak.ui.forwardnode.ProfileDropDown;

public class TextFieldsBindProject {
    private Binder<Project> binder;

    private TextField textResearchName;
    private TextField textSecret;
    private ProfileDropDown profileDropDown;

    public TextFieldsBindProject() {
        textResearchName = new TextField();
        textSecret = new TextField();
        profileDropDown = new ProfileDropDown();

        binder = setBinder();
    }

    private Binder<Project> setBinder() {
        Binder<Project> binder = new BeanValidationBinder<>(Project.class);
        binder.forField(textResearchName)
                .withValidator(StringUtils::isNotBlank,"Research name is mandatory")
                .bind(Project::getName, Project::setName);
        binder.forField(textSecret)
                .withValidator(StringUtils::isNotBlank,"Secret is mandatory")
                .withValidator(HMAC::validateKey, "Secret is not valid")
                .bind(project -> {
                    if (project.getSecret() != null) {
                        String hexKey = HMAC.byteToHex(project.getSecret());
                        return HMAC.showHexKey(hexKey);
                    }
                    return null;
                }, (project, s) -> {
                    project.setSecret(HMAC.hexToByte(s.replaceAll("-", "")));
                });
        binder.forField(profileDropDown)
                .withValidator(profilePipe -> profilePipe != null,
                        "Choose the de-identification profile\n")
                .bind(Project::getProfile, Project::setProfile);
        return binder;
    }

    public Binder<Project> getBinder() {
        return binder;
    }

    public TextField getTextResearchName() {
        return textResearchName;
    }

    public TextField getTextSecret() {
        return textSecret;
    }

    public ProfileDropDown getProfileDropDown() {
        return profileDropDown;
    }
}