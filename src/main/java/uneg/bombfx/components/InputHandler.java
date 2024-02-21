package uneg.bombfx.components;

import javafx.scene.input.KeyCode;

/**
 * InputHandler
 */
public class InputHandler {
    public class Input {
        public KeyCode kCode;
        public Boolean pressed = false;

        public Input(KeyCode kCode) {
            this.kCode = kCode;
        }
    }

    public enum InputOrder {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        BOMB,
        SPRINT,
        SPECIAL;
    }

    public Input[] inputs = new Input[InputOrder.values().length];

    public InputHandler() {
        inputs[InputOrder.UP.ordinal()] = new Input(KeyCode.W);
        inputs[InputOrder.DOWN.ordinal()] = new Input(KeyCode.S);
        inputs[InputOrder.LEFT.ordinal()] = new Input(KeyCode.A);
        inputs[InputOrder.RIGHT.ordinal()] = new Input(KeyCode.D);

        inputs[InputOrder.BOMB.ordinal()] = new Input(KeyCode.J);
        inputs[InputOrder.SPRINT.ordinal()] = new Input(KeyCode.K);
        inputs[InputOrder.SPECIAL.ordinal()] = new Input(KeyCode.L);
    }

    public void inputPressed(KeyCode kCode) {
        for (Input i : inputs) {
            if (i.kCode != kCode)
                continue;

            if (!i.pressed)
                i.pressed = true;
        }
    }

    public void inputReleased(KeyCode kCode) {
        for (Input i : inputs) {
            if (i.kCode != kCode)
                continue;

            if (i.pressed)
                i.pressed = false;
        }
    }

    public Input getInput(InputOrder action) {
        return inputs[action.ordinal()];
    }
}
