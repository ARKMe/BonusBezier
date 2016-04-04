package bello.andrea.bonusbezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRelativeLayout extends RelativeLayout implements View.OnTouchListener{

    Paint paint;
    private int startX;
    private int startY;
    Path path;

    View startPathMarker;
    View endPathMarker;

    private ArrayList<TextView> markers;

    public MyRelativeLayout(Context context) {
        super(context);
        init();
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        setWillNotDraw(false);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(ContextCompat.getColor(getContext(), android.R.color.black));

        markers = new ArrayList<>();
        path = new Path();

        //addPathEnds();
        addMarker();
    }

    private void addPathEnds() {
        startPathMarker = buildPathEndMarker(getWidth() / 2, getHeight()/3);
        endPathMarker = buildPathEndMarker(getWidth() / 2, getHeight()*2/3);
        addView(startPathMarker);
        addView(endPathMarker);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(startPathMarker == null || endPathMarker == null)
            addPathEnds();

        super.onLayout(changed, l, t, r, b);
    }

    private View buildPathEndMarker(int marginLeft, int marginTop){
        View view = new View(getContext());
        view.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.end_marker));
        view.setOnTouchListener(this);

        int markerSize = (int)getResources().getDimension(R.dimen.marker_size);
        LayoutParams layoutParams = new LayoutParams(markerSize, markerSize);
        layoutParams.setMargins(marginLeft, marginTop, -markerSize, -markerSize);
        view.setLayoutParams(layoutParams);

        return view;
    }

    public void changeCurve(){
        if (markers.size() == 1){
            addMarker();
        } else if(markers.size() == 2){
            removeMarker(1);
        }
    }

    private void addMarker(){
        TextView marker = new TextView(getContext());
        marker.setGravity(Gravity.CENTER);
        marker.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        marker.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.point_marker));
        marker.setOnTouchListener(this);
        marker.setText("" + (markers.size() + 1));

        int markerSize = (int)getResources().getDimension(R.dimen.marker_size);
        LayoutParams layoutParams = new LayoutParams(markerSize, markerSize);
        marker.setLayoutParams(layoutParams);

        addView(marker);
        markers.add(marker);
    }

    private void removeMarker(int index){
        TextView marker = markers.remove(index);
        removeView(marker);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Point startPath = getMarkerCenter(startPathMarker);
        Point endPath = getMarkerCenter(endPathMarker);

        if (markers.size() == 1) {
            Point quadPoint = getMarkerCenter(markers.get(0));
            path.reset();
            path.moveTo(startPath.x, startPath.y);
            path.quadTo(quadPoint.x, quadPoint.y, endPath.x, endPath.y);
            canvas.drawPath(path, paint);
        } else if (markers.size() == 2) {
            path.reset();
            Point cubicFirstPoint = getMarkerCenter(markers.get(0));
            Point cubicSecondPoint = getMarkerCenter(markers.get(1));

            path.moveTo(startPath.x, startPath.y);
            path.cubicTo(cubicFirstPoint.x, cubicFirstPoint.y, cubicSecondPoint.x, cubicSecondPoint.y, endPath.x, endPath.y);
            canvas.drawPath(path, paint);
        }

        super.onDraw(canvas);
    }

    private Point getMarkerCenter(View view){
        int x = (int)(view.getX() + view.getWidth()/2);
        int y = (int)(view.getY() + view.getHeight()/2);

        return new Point(x, y);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int currentX = (int) event.getRawX();
        final int currentY = (int) event.getRawY();

        switch (event.getAction()) {

            case android.view.MotionEvent.ACTION_DOWN:
                startX = currentX;
                startY = currentY;
                break;

            case android.view.MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = layoutParams.leftMargin + currentX - startX;
                layoutParams.topMargin = layoutParams.topMargin + currentY - startY;
                startX = currentX;
                startY = currentY;
                view.setLayoutParams(layoutParams);
                break;

        }
        // Schedules a repaint for the root Layout.
        invalidate();
        return true;
    }
}
