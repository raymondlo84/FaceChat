/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 80.0f;
    private static final float ID_Y_OFFSET = 60.0f;
    private static final float ID_X_OFFSET = -70.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private static final float EYE_SIZE = 10.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    private String captions;
    private String mstatus;

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        captions = "";
        mstatus = "";
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    public void setCaptions(String captions){
        this.captions = captions;
    }
    public void setStatus(String status){
        this.mstatus = status;
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET*2, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - 3*ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x - 3*ID_X_OFFSET, y - ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - 3*ID_X_OFFSET, y - ID_Y_OFFSET*3, mIdPaint);
        canvas.drawText("EulerZ: " + String.format("%.2f", face.getEulerZ()),  x - 3*ID_X_OFFSET, y - ID_Y_OFFSET*4, mIdPaint);
        canvas.drawText("EulerY: " + String.format("%.2f", face.getEulerY()),  x - 3*ID_X_OFFSET, y - ID_Y_OFFSET*5, mIdPaint);
        canvas.drawText(captions,  x + 3*ID_X_OFFSET, y + ID_Y_OFFSET*8, mIdPaint);
        canvas.drawText(mstatus,  100, 100, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);

        // Draws all features supported.
        List<Landmark> face_features = face.getLandmarks();
        for(int i = 0; i<face_features.size(); i++){
            Landmark feature = face_features.get(i);
            int type = feature.getType();
            float x_eye, y_eye;
            x_eye = translateX(feature.getPosition().x);
            y_eye = translateY(feature.getPosition().y);
            canvas.drawRect(x_eye-EYE_SIZE,
                    y_eye-EYE_SIZE,
                    x_eye+EYE_SIZE,
                    y_eye+EYE_SIZE, mBoxPaint);
//            switch (type){
//                case Landmark.LEFT_EYE:
//                    x_eye = translateX(feature.getPosition().x);
//                    y_eye = translateY(feature.getPosition().y);
//                    canvas.drawRect(x_eye-EYE_SIZE,
//                            y_eye-EYE_SIZE,
//                            x_eye+EYE_SIZE,
//                            y_eye+EYE_SIZE, mBoxPaint);
//                    break;
//                case Landmark.RIGHT_EYE:
//                    x_eye = translateX(feature.getPosition().x);
//                    y_eye = translateY(feature.getPosition().y);
//                    canvas.drawRect(x_eye-EYE_SIZE,
//                            y_eye-EYE_SIZE,
//                            x_eye+EYE_SIZE,
//                            y_eye+EYE_SIZE, mBoxPaint);
//                    break;
//                default:
//                    break;
//            }
        }
    }
}
