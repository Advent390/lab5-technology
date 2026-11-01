package ua.edu.nuos.lab5technology.data;

/** Група параметрів (розміри, екран тощо). */
public record ParameterGroup(int id, String name) {
    @Override
    public String toString() {
        return name;
    }
}
