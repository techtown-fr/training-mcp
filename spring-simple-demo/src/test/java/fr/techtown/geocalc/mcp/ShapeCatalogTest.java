package fr.techtown.geocalc.mcp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.Test;

class ShapeCatalogTest {

    private final ShapeCatalog catalog = new ShapeCatalog();

    @Test
    void listsTheThreeSupportedShapes() {
        assertThat(catalog.listShapes())
            .extracting(ShapeCatalog.ShapeDefinition::id)
            .containsExactly("circle", "rectangle", "triangle");
    }

    @Test
    void validatesATriangleCalculation() {
        var validation = catalog.validate(
            "triangle",
            Map.of("base", 10.0, "height", 4.0),
            20.0
        );

        assertThat(validation.valid()).isTrue();
        assertThat(validation.expected()).isEqualTo(20.0);
        assertThat(validation.difference()).isZero();
    }

    @Test
    void rejectsMissingDimensions() {
        assertThatThrownBy(() ->
            catalog.validate("circle", Map.of(), 12.0)
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("radius");
    }

    @Test
    void rejectsNonFiniteResults() {
        assertThatThrownBy(() ->
            catalog.validate("circle", Map.of("radius", 2.0), Double.NaN)
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("fini");
    }
}
