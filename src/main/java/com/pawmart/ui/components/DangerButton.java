package com.pawmart.ui.components;

import com.pawmart.ui.theme.PawMartTheme;

import java.awt.Color;

/**
 * Soft crimson action button for destructive/deactivation actions.
 */
public class DangerButton extends PrimaryButton {

    public DangerButton(String text) {
        super(text, PawMartTheme.STATUS_DANGER, new Color(220, 38, 38));
    }
}
