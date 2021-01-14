package org.karnak.backend.model.standard;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.karnak.backend.exception.ModuleNotFoundException;
import org.karnak.backend.model.dicominnolitics.StandardModuleToAttributes;
import org.karnak.backend.model.dicominnolitics.jsonModuleToAttribute;

public class ModuleToAttributes {

  /*
   * <moduleID, <TagPath, Attribute>>
   * */
  private final Map<String, Map<String, Attribute>> HMapModuleAttributes;

  public ModuleToAttributes() {
    HMapModuleAttributes =
        initializeAttributes(StandardModuleToAttributes.readJsonModuleToAttributes());
  }

  private Map<String, Map<String, Attribute>> initializeAttributes(
      jsonModuleToAttribute[] moduleToAttributes) {
    Map<String, Map<String, Attribute>> HMapModuleAttributes = new HashMap<>();

    for (jsonModuleToAttribute moduleToAttribute : moduleToAttributes) {
      Attribute attribute =
          new Attribute(
              moduleToAttribute.getPath(),
              moduleToAttribute.getType(),
              moduleToAttribute.getModuleId());

      String moduleKey = moduleToAttribute.getModuleId();
      if (!HMapModuleAttributes.containsKey(moduleKey)) {
        HMapModuleAttributes.put(moduleKey, new HashMap<>());
      }
      HMapModuleAttributes.get(moduleKey).put(attribute.getTagPath(), attribute);
    }

    return HMapModuleAttributes;
  }

  public Map<String, Attribute> getAttributesByModule(String moduleID) {
    return HMapModuleAttributes.get(moduleID);
  }

  public Map<String, Attribute> getModuleAttributesByType(String moduleID, String type)
      throws ModuleNotFoundException {
    Map<String, Attribute> attributes = HMapModuleAttributes.get(moduleID);
    if (attributes == null) {
      throw new ModuleNotFoundException(
          String.format("Unable to get module attributes. Could not find the module %s", moduleID));
    }
    return attributes.entrySet().stream()
        .filter(entry -> type.equals(entry.getValue().getType()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}