package fr.techtown.geocalc.mcp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ShapeCatalog {

    private static final List<ShapeDefinition> SHAPES = List.of(
        new ShapeDefinition("circle", "Cercle", "π × radius²", List.of("radius")),
        new ShapeDefinition(
            "rectangle",
            "Rectangle",
            "width × height",
            List.of("width", "height")
        ),
        new ShapeDefinition(
            "triangle",
            "Triangle",
            "(base × height) / 2",
            List.of("base", "height")
        )
    );

    public List<ShapeDefinition> listShapes() {
        return SHAPES;
    }

    public ShapeDefinition getFormula(String shape) {
        String id = normalize(shape);
        return SHAPES.stream()
            .filter(definition -> definition.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Forme inconnue : " + shape + ". Valeurs acceptées : circle, rectangle, triangle"
            ));
    }

    public ValidationResult validate(
            String shape,
            Map<String, Double> dimensions,
            double submittedResult) {
        ShapeDefinition definition = getFormula(shape);
        requireFinite("result", submittedResult);

        double expected = switch (definition.id()) {
            case "circle" -> Math.PI * square(dimension(dimensions, "radius"));
            case "rectangle" ->
                dimension(dimensions, "width") * dimension(dimensions, "height");
            case "triangle" ->
                dimension(dimensions, "base") * dimension(dimensions, "height") / 2.0;
            default -> throw new IllegalStateException("Forme non implémentée");
        };

        double difference = Math.abs(expected - submittedResult);
        double tolerance = Math.max(1.0e-9, Math.abs(expected) * 1.0e-9);
        return new ValidationResult(
            definition.id(),
            expected,
            submittedResult,
            difference,
            difference <= tolerance
        );
    }

    private static String normalize(String shape) {
        if (shape == null || shape.isBlank()) {
            throw new IllegalArgumentException("La forme est obligatoire");
        }
        return shape.trim().toLowerCase(Locale.ROOT);
    }

    private static double dimension(Map<String, Double> dimensions, String name) {
        if (dimensions == null || !dimensions.containsKey(name)) {
            throw new IllegalArgumentException("Dimension obligatoire : " + name);
        }
        Double value = dimensions.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Dimension obligatoire : " + name);
        }
        requireFinite(name, value);
        if (value <= 0) {
            throw new IllegalArgumentException(name + " doit être strictement positif");
        }
        return value;
    }

    private static void requireFinite(String name, double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(name + " doit être un nombre fini");
        }
    }

    private static double square(double value) {
        return value * value;
    }

    public record ShapeDefinition(
        String id,
        String label,
        String formula,
        List<String> requiredDimensions
    ) {
    }

    public record ValidationResult(
        String shape,
        double expected,
        double submitted,
        double difference,
        boolean valid
    ) {
    }
}
