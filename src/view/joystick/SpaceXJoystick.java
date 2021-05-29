package view.joystick;

/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.LongProperty;
import javafx.beans.property.LongPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;


/**
 * User: hansolo
 * Date: 31.05.20
 * Time: 08:18
 */
public class SpaceXJoystick extends Region {
    public static final double                    PREFERRED_WIDTH  = 500;
    public static final double                    PREFERRED_HEIGHT = 500;
    private static final double                    MINIMUM_WIDTH    = 50;
    private static final double                    MINIMUM_HEIGHT   = 50;
    private static final double                    MAXIMUM_WIDTH    = 1024;
    private static final double                    MAXIMUM_HEIGHT   = 1024;
    public  static final double                     HALF_PI          = Math.PI / 2.0;
    private static final double                    MAX_STEP_SIZE    = 10;
    public               double                    size;
    public               double                    center;
    public               Canvas                    background;
    public               GraphicsContext           ctx;
    public               Circle                    touchIndicator;
    public               Circle                    touchPoint;
    public               Arc                       touchN;
    public               Arc                       touchNW;
    public               Arc                       touchW;
    public               Arc                       touchSW;
    public               Arc                       touchS;
    public               Arc                       touchSE;
    public               Arc                       touchE;
    public               Arc                       touchNE;
    public               Pane                      pane;
    private              LockState                 _lockState;
    private              ObjectProperty<LockState> lockState;
    private              boolean                   _stickyMode;
    private              BooleanProperty           stickyMode;
    private              boolean                   _animated;
    private              BooleanProperty           animated;
    private              long                      _durationMillis;
    private              LongProperty              durationMillis;
    private              double                    _stepSize;
    private              DoubleProperty            stepSize;
    private              boolean                   _stepButtonsVisible;
    private              BooleanProperty           stepButtonsVisible;
    public               Color                     _inactiveColor;
    public               ObjectProperty<Color>     inactiveColor;
    public               Color                     _activeColor;
    public               ObjectProperty<Color>     activeColor;
    public               Color                     _lockedColor;
    public               ObjectProperty<Color>     lockedColor;
    public               Color                     translucentActiveColor;
    private              boolean                   _touched;
    private              BooleanProperty           touched;
    public               DoubleProperty            x;
    public               DoubleProperty            y;
    public               DoubleProperty            value;
    public               DoubleProperty            angle;
    public               double                    offsetX;
    public               double                    offsetY;


    // ******************** Constructors **************************************
    public SpaceXJoystick() {
        center                  = PREFERRED_WIDTH * 0.5;
        _lockState              = LockState.UNLOCKED;
        _stickyMode             = true;
        _animated               = false;
        _durationMillis         = 100;
        _stepSize               = 0.01;
        _stepButtonsVisible     = true;
        _inactiveColor          = Color.web("#506691");
        _activeColor            = Color.web("#CFF9FF");
        _lockedColor            = Color.web("#B36B6B");
        translucentActiveColor = Color.color(_activeColor.getRed(), _activeColor.getGreen(), _activeColor.getBlue(), 0.25);
        _touched                = false;
        x                       = new DoublePropertyBase() {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return SpaceXJoystick.this; }
            @Override public String getName() { return "valueX"; }
        };
        y                       = new DoublePropertyBase() {
            @Override protected void invalidated() {}
            @Override public Object getBean() { return SpaceXJoystick.this; }
            @Override public String getName() { return "valueY"; }
        };
        value                   = new DoublePropertyBase(0.0) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return SpaceXJoystick.this; }
            @Override public String getName() { return "value"; }
        };
        angle                   = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
            }
            @Override public Object getBean() { return SpaceXJoystick.this; }
            @Override public String getName() { return "angle"; }
        };
        offsetX                 = 0;
        offsetY                 = 0;
        initGraphics();
        registerListeners();

    }

    public double getValue() { return value.get(); }
    public void setValue(final double value) { this.value.set(value); }
    public DoubleProperty valueProperty() { return value; }

    public double getAngle() { return angle.get(); }
    public void setAngle(final double angle) { this.angle.set(angle); }
    public DoubleProperty angleProperty() { return angle; }

    public double getX() { return x.get(); }
    public void setX(final double x) { this.x.set(x); }
    public DoubleProperty xProperty() { return x; }

    public double getY() { return y.get(); }
    public void setY(final double y) { this.y.set(y); }
    public DoubleProperty yProperty() { return y; }

    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
                Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }


        background = new Canvas(0.7 * PREFERRED_WIDTH, 0.7 * PREFERRED_HEIGHT);
        background.setMouseTransparent(true);
        ctx        = background.getGraphicsContext2D();

        touchN  = createArc(0);
        touchNW = createArc(45);
        touchW  = createArc(90);
        touchSW = createArc(135);
        touchS  = createArc(180);
        touchSE = createArc(225);
        touchE  = createArc(270);
        touchNE = createArc(315);

        touchIndicator = new Circle();
        touchIndicator.setFill(Color.TRANSPARENT);
        touchIndicator.setStroke(getInactiveColor());
        touchIndicator.setMouseTransparent(true);

        touchPoint = new Circle();
        touchPoint.setFill(Color.TRANSPARENT);
        touchPoint.setStroke(getActiveColor());

        pane = new Pane(background, touchN, touchNW, touchW, touchSW, touchS, touchSE, touchE, touchNE, touchIndicator, touchPoint);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }


    public LockState getLockState() { return null == lockState ? _lockState : lockState.get(); }

    public boolean isStickyMode() { return null == stickyMode ? _stickyMode : stickyMode.get(); }

    public boolean isAnimated() { return null == animated ? _animated : animated.get(); }

    public long getDurationMillis() { return null == durationMillis ? _durationMillis : durationMillis.get(); }

    public LongProperty durationMillisProperty() {
        if (null == durationMillis) {
            durationMillis = new LongPropertyBase(_durationMillis) {
                @Override protected void invalidated() { set(clamp(10, 1000, get())); }
                @Override public Object getBean() { return SpaceXJoystick.this; }
                @Override public String getName() { return "durationMillis"; }
            };
        }
        return durationMillis;
    }

    public DoubleProperty stepSizeProperty() {
        if (null == stepSize) {
            stepSize = new DoublePropertyBase(_stepSize) {
                @Override protected void invalidated() { set(clamp(0.001, MAX_STEP_SIZE, get())); }
                @Override public Object getBean() { return SpaceXJoystick.this; }
                @Override public String getName() { return "stepSizeX"; }
            };
        }
        return stepSize;
    }

    public void setStepButtonsVisible(final boolean stepButtonsVisible) {
        if (null == this.stepButtonsVisible) {
            _stepButtonsVisible = stepButtonsVisible;
            touchN.setVisible(stepButtonsVisible);
            touchNW.setVisible(stepButtonsVisible);
            touchW.setVisible(stepButtonsVisible);
            touchSW.setVisible(stepButtonsVisible);
            touchS.setVisible(stepButtonsVisible);
            touchSE.setVisible(stepButtonsVisible);
            touchE.setVisible(stepButtonsVisible);
            touchNE.setVisible(stepButtonsVisible);
            redraw();
        } else {
            this.stepButtonsVisible.set(stepButtonsVisible);
        }
    }
    public BooleanProperty stepButtonsVisibleProperty() {
        if (null == stepButtonsVisible) {
            stepButtonsVisible = new BooleanPropertyBase(_stepButtonsVisible) {
                @Override protected void invalidated() {
                    touchN.setVisible(get());
                    touchNW.setVisible(get());
                    touchW.setVisible(get());
                    touchSW.setVisible(get());
                    touchS.setVisible(get());
                    touchSE.setVisible(get());
                    touchE.setVisible(get());
                    touchNE.setVisible(get());
                    redraw();
                }
                @Override public Object getBean() { return SpaceXJoystick.this; }
                @Override public String getName() { return "stepButtonsVisible"; }
            };
        }
        return stepButtonsVisible;
    }

    public Color getInactiveColor() { return null == inactiveColor ? _inactiveColor : inactiveColor.get(); }
    public void setInactiveColor(final Color inactiveColor) {
        if (null == this.inactiveColor) {
            _inactiveColor = inactiveColor;
            redraw();
        } else {
            this.inactiveColor.set(inactiveColor);
        }
    }
    public ObjectProperty<Color> inactiveColorProperty() {
        if (null == inactiveColor) {
            inactiveColor = new ObjectPropertyBase<Color>(_inactiveColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return SpaceXJoystick.this; }
                @Override public String getName() { return "inactiveColor"; }
            };
            _inactiveColor = null;
        }
        return inactiveColor;
    }

    public Color getActiveColor() { return null == activeColor ? _activeColor : activeColor.get(); }
    public void setActiveColor(final Color activeColor) {
        if (null == this.activeColor) {
            _activeColor            = activeColor;
            translucentActiveColor = Color.color(_activeColor.getRed(), _activeColor.getGreen(), _activeColor.getBlue(), 0.25);
            redraw();
        } else {
            this.activeColor.set(activeColor);
        }
    }
    public ObjectProperty<Color> activeColorProperty() {
        if (null == activeColor) {
            activeColor = new ObjectPropertyBase<>(_activeColor) {
                @Override protected void invalidated() {
                    translucentActiveColor = Color.color(get().getRed(), get().getGreen(), get().getBlue(), 0.25);
                    redraw();
                }
                @Override public Object getBean() { return SpaceXJoystick.this; }
                @Override public String getName() { return "activeColor"; }
            };
            _activeColor = null;
        }
        return activeColor;
    }

    public Color getLockedColor() { return null == lockedColor ? _lockedColor : lockedColor.get(); }
    public void setLockedColor(final Color lockedColor) {
        if (null == this.lockedColor) {
            _lockedColor = lockedColor;
            redraw();
        } else {
            this.lockedColor.set(lockedColor);
        }
    }
    public ObjectProperty<Color> lockedColorProperty() {
        if (null == lockedColor) {
            lockedColor = new ObjectPropertyBase<>(_lockedColor) {
                @Override
                protected void invalidated() {
                    redraw();
                }

                @Override
                public Object getBean() {
                    return SpaceXJoystick.this;
                }

                @Override
                public String getName() {
                    return "lockedColor";
                }
            };
            _lockedColor = null;
        }
        return lockedColor;
    }

    public boolean isTouched() { return null == touched ? _touched : touched.get(); }

    private void reset() {
        if (!isStickyMode()) {
            if (isAnimated()) {
                KeyValue kvX0 = new KeyValue(touchPoint.centerXProperty(), touchPoint.getCenterX(), Interpolator.EASE_OUT);
                KeyValue kvY0 = new KeyValue(touchPoint.centerYProperty(), touchPoint.getCenterY(), Interpolator.EASE_OUT);
                KeyValue kvV0 = new KeyValue(value, value.get(), Interpolator.EASE_OUT);
                KeyValue kvX1 = new KeyValue(touchPoint.centerXProperty(), 0.5 * size, Interpolator.EASE_OUT);
                KeyValue kvY1 = new KeyValue(touchPoint.centerYProperty(), 0.5 * size, Interpolator.EASE_OUT);
                KeyValue kvV1 = new KeyValue(value, 0, Interpolator.EASE_OUT);
                KeyFrame kf0  = new KeyFrame(Duration.ZERO, kvX0, kvY0, kvV0);
                KeyFrame kf1  = new KeyFrame(Duration.millis(getDurationMillis()), kvX1, kvY1, kvV1);
            } else {
                touchPoint.setCenterX(center);
                touchPoint.setCenterY(center);
                resetTouchButtons();
                value.set(0);
                angle.set(0);
            }
        }
    }

    private void resetTouchButtons() {
        Color inactiveColor = getInactiveColor();
        switch (getLockState()) {
            case X_LOCKED -> {
                touchN.setStroke(translucentActiveColor);
                touchNW.setStroke(translucentActiveColor);
                touchW.setStroke(inactiveColor);
                touchSW.setStroke(translucentActiveColor);
                touchS.setStroke(translucentActiveColor);
                touchSE.setStroke(translucentActiveColor);
                touchE.setStroke(inactiveColor);
                touchNE.setStroke(translucentActiveColor);
            }
            case Y_LOCKED -> {
                touchN.setStroke(inactiveColor);
                touchNW.setStroke(translucentActiveColor);
                touchW.setStroke(translucentActiveColor);
                touchSW.setStroke(translucentActiveColor);
                touchS.setStroke(inactiveColor);
                touchSE.setStroke(translucentActiveColor);
                touchE.setStroke(translucentActiveColor);
                touchNE.setStroke(translucentActiveColor);
            }
            default -> {
                touchN.setStroke(inactiveColor);
                touchNW.setStroke(inactiveColor);
                touchW.setStroke(inactiveColor);
                touchSW.setStroke(inactiveColor);
                touchS.setStroke(inactiveColor);
                touchSE.setStroke(inactiveColor);
                touchE.setStroke(inactiveColor);
                touchNE.setStroke(inactiveColor);
            }
        }
    }

    public void setXY(final double newX, final double newY) {
        double x   = clamp(size * 0.15, size * 0.85, newX);
        double y   = clamp(size * 0.15, size * 0.85, newY);
        double dx  = x - center;
        double dy  = -(y - center);
        double rad = Math.atan2(dy, dx) + HALF_PI;
        double phi = Math.toDegrees(rad - Math.PI);
        if (phi < 0) { phi += 360.0; }
        setAngle(phi);
        double r    = Math.sqrt(dx * dx + dy * dy);
        double maxR = size * 0.35;
        if (r > maxR) {
            x = -Math.cos(rad + HALF_PI) * maxR + center;
            y = Math.sin(rad + HALF_PI) * maxR + center;
            r = maxR;
        }
        setX(-Math.cos(rad + HALF_PI));
        setY(-Math.sin(rad + HALF_PI));

        touchPoint.setCenterX(x);
        touchPoint.setCenterY(y);
        setValue(r / maxR);

        //redraw();
    }

    private Arc createArc(final double startAngle) {
        Arc arc  = new Arc(0.5 * size, 0.5 * size, 0.455 * size, 0.455 * size, startAngle + 90 - 18.5, 37);
        arc.setFill(Color.TRANSPARENT);
        arc.setStrokeLineCap(StrokeLineCap.BUTT);
        return arc;
    }

    private double clamp(final double min, final double max, final double value) {
        if (value < min) { return min; }
        return Math.min(value, max);
    }
    private long clamp(final long min, final long max, final long value) {
        if (value < min) { return min; }
        return Math.min(value, max);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = Math.min(width, height);
        center = size * 0.5;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            background.setWidth(0.7 * size);
            background.setHeight(0.7 * size);
            background.relocate(0.15 *size, 0.15 * size);

            resizeArc(touchN);
            resizeArc(touchNW);
            resizeArc(touchW);
            resizeArc(touchSW);
            resizeArc(touchS);
            resizeArc(touchSE);
            resizeArc(touchE);
            resizeArc(touchNE);

            touchIndicator.setRadius(0.4 * size);
            touchIndicator.setCenterX(center);
            touchIndicator.setCenterY(center);

            touchPoint.setRadius(0.05 * size);
            touchPoint.setCenterX(center + offsetX);
            touchPoint.setCenterY(center + offsetY);

            redraw();
        }
    }

    private void resizeArc(final Arc arc) {
        arc.setCenterX(center);
        arc.setCenterY(center);
        arc.setRadiusX(0.455 * size);
        arc.setRadiusY(0.455 * size);
        arc.setStrokeWidth(0.084 * size);
    }

    public void drawBackground() {
        double w = background.getWidth();
        double h = background.getHeight();
        ctx.clearRect(0, 0, background.getWidth(), background.getHeight());
        ctx.setFill(getInactiveColor());
        ctx.fillOval(0, 0, w, h);
        ctx.setFill(Color.TRANSPARENT);
        ctx.setStroke(LockState.X_LOCKED == getLockState() || LockState.Y_LOCKED == getLockState() ? getLockedColor() : translucentActiveColor);
        ctx.strokeLine(0.15 * w, 0.15 * h, 0.85 * w, 0.85 * h);
        ctx.strokeLine(0.85 * w, 0.15 * h, 0.15 * w, 0.85 * h);
        ctx.setStroke(translucentActiveColor);
        ctx.strokeOval(0.42857143 * w, 0.42857143 * h, 0.14285714 * w, 0.14285714 * h);
        ctx.setStroke(LockState.Y_LOCKED == getLockState() ? getLockedColor() : translucentActiveColor);
        ctx.strokeLine(0, 0.5 * h, w, 0.5 *h);
        ctx.setStroke(LockState.X_LOCKED == getLockState() ? getLockedColor() : translucentActiveColor);
        ctx.strokeLine(0.5 * w, 0, 0.5 * w, h);

        ctx.save();
        double value            = getValue();
        double chevronHalfWidth = 0.05 * w;
        double chevronHeight    = 0.04 * h;
        double center           = 0.5 * w;
        double offsetY          = h - chevronHeight * 0.25;
        double chevronStepY     = 1.22 * chevronHeight;
        ctx.translate(center, center);
        ctx.rotate(-getAngle());
        ctx.translate(-center, -center);
        ctx.setStroke(getActiveColor());
        ctx.setLineWidth(0.015 * h);
        ctx.setLineCap(StrokeLineCap.ROUND);
        ctx.setLineJoin(StrokeLineJoin.ROUND);
        int counter = 0;
        for (double i = 0.0 ; i < value - 0.1 ; i += 0.1) {
            ctx.strokeLine(center - chevronHalfWidth, offsetY - counter * chevronStepY, center, offsetY - (counter + 1) * chevronStepY);
            ctx.strokeLine(center, offsetY - (counter + 1) * chevronStepY, center + chevronHalfWidth, offsetY - counter * chevronStepY);
            counter += 1;
        }
        ctx.restore();
    }

    public void redraw() {
        drawBackground();
        Color activeColor   = getActiveColor();
        Color inactiveColor = getInactiveColor();
        switch (getLockState()) {
            case X_LOCKED -> {
                touchN.setStroke(translucentActiveColor);
                touchNW.setStroke(translucentActiveColor);
                touchW.setStroke(touchW.isHover() ? activeColor : inactiveColor);
                touchSW.setStroke(translucentActiveColor);
                touchS.setStroke(translucentActiveColor);
                touchSE.setStroke(translucentActiveColor);
                touchE.setStroke(touchE.isHover() ? activeColor : inactiveColor);
                touchNE.setStroke(translucentActiveColor);
            }
            case Y_LOCKED -> {
                touchN.setStroke(touchN.isHover() ? activeColor : inactiveColor);
                touchNW.setStroke(translucentActiveColor);
                touchW.setStroke(translucentActiveColor);
                touchSW.setStroke(translucentActiveColor);
                touchS.setStroke(touchS.isHover() ? activeColor : inactiveColor);
                touchSE.setStroke(translucentActiveColor);
                touchE.setStroke(translucentActiveColor);
                touchNE.setStroke(translucentActiveColor);
            }
            default -> {
                touchN.setStroke(touchN.isHover() ? activeColor : inactiveColor);
                touchNW.setStroke(touchNW.isHover() ? activeColor : inactiveColor);
                touchW.setStroke(touchW.isHover() ? activeColor : inactiveColor);
                touchSW.setStroke(touchSW.isHover() ? activeColor : inactiveColor);
                touchS.setStroke(touchS.isHover() ? activeColor : inactiveColor);
                touchSE.setStroke(touchSE.isHover() ? activeColor : inactiveColor);
                touchE.setStroke(touchE.isHover() ? activeColor : inactiveColor);
                touchNE.setStroke(touchNE.isHover() ? activeColor : inactiveColor);
            }
        }

        touchPoint.setFill(isTouched() ? activeColor : Color.TRANSPARENT);
        touchIndicator.setStroke(isTouched() ? activeColor : inactiveColor);
    }
}