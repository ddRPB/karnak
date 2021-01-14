package org.karnak.frontend.forwardnode;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import java.util.HashSet;
import java.util.Set;
import org.karnak.backend.data.entity.DestinationEntity;
import org.karnak.backend.data.entity.SOPClassUIDEntity;
import org.karnak.backend.service.SOPClassUIDDataProvider;
import org.vaadin.gatanaso.MultiselectComboBox;

public class FilterBySOPClassesForm extends HorizontalLayout {

  private final SOPClassUIDDataProvider sopClassUIDDataProvider;
  private final MultiselectComboBox<String> sopFilter;
  private final Checkbox filterBySOPClassesCheckbox;
  private final Binder<DestinationEntity> binder;

  public FilterBySOPClassesForm(Binder<DestinationEntity> binder) {
    this.binder = binder;
    sopClassUIDDataProvider = new SOPClassUIDDataProvider();
    filterBySOPClassesCheckbox = new Checkbox("Authorized SOPs");
    sopFilter = new MultiselectComboBox<>();
    setElements();
    setBinder();
    add(filterBySOPClassesCheckbox, sopFilter);
  }

  private void setElements() {
    filterBySOPClassesCheckbox.setMinWidth("25%");
    sopFilter.setMinWidth("70%");

    filterBySOPClassesCheckbox.setValue(false);
    sopFilter.onEnabledStateChanged(false);

    filterBySOPClassesCheckbox.addValueChangeListener(
        checkboxBooleanComponentValueChangeEvent ->
            sopFilter.onEnabledStateChanged(checkboxBooleanComponentValueChangeEvent.getValue()));

    sopFilter.setItems(sopClassUIDDataProvider.getAllSOPClassUIDsName());
  }

  private void setBinder() {
    binder
        .forField(sopFilter)
        .withValidator(
            listOfSOPFilter -> !listOfSOPFilter.isEmpty() || !filterBySOPClassesCheckbox.getValue(),
            "No filter are applied\n")
        .bind(
            DestinationEntity::getSOPClassUIDFiltersName,
            (destination, sopClassNames) -> {
              Set<SOPClassUIDEntity> newSOPClassUIDEntities = new HashSet<>();
              sopClassNames.forEach(
                  sopClasseName -> {
                    SOPClassUIDEntity sopClassUIDEntity =
                        sopClassUIDDataProvider.getByName(sopClasseName);
                    newSOPClassUIDEntities.add(sopClassUIDEntity);
                  });
              destination.setSOPClassUIDFilters(newSOPClassUIDEntities);
            });

    binder
        .forField(filterBySOPClassesCheckbox) //
        .bind(DestinationEntity::getFilterBySOPClasses, DestinationEntity::setFilterBySOPClasses);
  }
}