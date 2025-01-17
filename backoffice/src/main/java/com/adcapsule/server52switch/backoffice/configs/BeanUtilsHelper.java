package com.adcapsule.server52switch.backoffice.configs;
import java.lang.reflect.Field;

public class BeanUtilsHelper {

    /**
     * A global helper method to update non-null fields from input to the existing entity.
     *
     * @param existingEntity The entity object that will be updated.
     * @param updatedFields  The input object containing the updated values.
     * @param <T>            The type of the entity (e.g., Employee).
     */
    public static <T> void updateEntityFields(T existingEntity, T updatedFields) {
        // System.out.println("updateEntityFields method called"); // Debugging statement
        
        if (existingEntity == null || updatedFields == null) {
            // System.out.println("Either existingEntity or updatedFields is null");
            throw new IllegalArgumentException("Neither entity nor updated fields can be null");
        }
        //     // Reflect on the class of the existing entity
        // Class<?> entityClass = existingEntity.getClass();
        // System.out.println("Reflecting on class: " + entityClass.getName());
        // Use reflection to iterate over all fields of the entity
        for (Field field : existingEntity.getClass().getDeclaredFields()) {
            // System.out.println("Found field: " + field.getName()+ " of type: " + field.getType()); // Debugging statement
            try {
                field.setAccessible(true);
                // Check if the field exists in updatedFields
                Field updatedField = null;
                try {
                    updatedField = updatedFields.getClass().getDeclaredField(field.getName());
                } catch (NoSuchFieldException e) {
                    // Field does not exist in updatedFields, so we skip it
                    continue;
                }
                // System.out.println("Found field in updatedFields: "+ updatedField);
                updatedField.setAccessible(true);
                Object updatedValue = updatedField.get(updatedFields);

                // Only update the field if it's not null
                if (updatedValue != null) {
                //     // Debug: Print what will be set
                // System.out.println("Updating field: " + updatedField.getName() + " to value: " + updatedValue);
                    field.set(existingEntity, updatedValue);
                }
            }catch (IllegalAccessException e) {
                System.err.println("Error accessing field: " + field.getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Unexpected error with field: " + field.getName());
                e.printStackTrace();
            }
        }
    }

    
}
