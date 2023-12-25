module uneg.bombfx {
    requires javafx.fxml;
    requires javafx.controls;

    opens uneg.bombfx.views to javafx.fxml;

    exports uneg.bombfx;
}
