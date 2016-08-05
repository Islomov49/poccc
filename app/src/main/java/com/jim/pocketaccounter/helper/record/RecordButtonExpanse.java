package com.jim.pocketaccounter.helper.record;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;

import com.jim.pocketaccounter.R;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;

import java.text.DecimalFormat;
import java.util.Calendar;

public class RecordButtonExpanse {
	public static final int TOP_LEFT = 0, SIMPLE = 1, LEFT_SIMPLE = 2, LEFT_BOTTOM = 3, BOTTOM_SIMPLE = 4,
							BOTTOM_RIGHT = 5, TOP_RIGHT = 6, TOP_SIMPLE = 7, RIGHT_SIMPLE = 8;
	private boolean pressed = false;
	private RectF container;
	private int type;
	private Path shape;
	private Bitmap shadow;
	private float radius, clearance;
	private Context context;
	private RootCategory category;
	private float aLetterHeight;
	private Calendar date;
	public RecordButtonExpanse(Context context, int type, Calendar date) {
		this.context = context;
		this.date = (Calendar) date.clone();
		clearance = context.getResources().getDimension(R.dimen.one_dp);
		shape = new Path();
		this.type = type;
		Paint paint = new Paint();
		paint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
		Rect bounds = new Rect();
		paint.getTextBounds("A", 0, "A".length(), bounds);
		aLetterHeight = bounds.height();
	}
	public void setBounds(float left, float top, float right, float bottom, float radius) {
		container = new RectF(left, top, right, bottom);
		this.radius = radius;
		switch(type) {
		case TOP_LEFT:
			initTopLeft();
			break;
		case TOP_SIMPLE:
			initSimple();
			break;
		case TOP_RIGHT:
			initTopRight();
			break;
		case LEFT_SIMPLE:
			initLeftSimple();
			break;
		case SIMPLE:
			initSimple();
			break;
		case RIGHT_SIMPLE:
			initSimple();
			break;
		case LEFT_BOTTOM:
			initLeftBottom();
			break;
		case BOTTOM_SIMPLE:
			initBottomSimple();
			break;
		case BOTTOM_RIGHT:
			initBottomRight();
			break;
		}
	}
	private void initTopRight() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right-2*radius, container.top);
		shape.arcTo(new RectF(container.right-2*radius, container.top, container.right, container.top+2*radius), 270.0f, 90.0f);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
	}
	private void initBottomRight() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom-2*radius);
		shape.arcTo(new RectF(container.right-2*radius, container.bottom-2*radius, container.right, container.bottom), 0.0f, 90.0f);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapHeight = container.height()/5;
		bitmapWidth = bitmapHeight*6-radius/2;
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bottom_shadow);
		shadow = Bitmap.createScaledBitmap(temp, (int)(bitmapWidth-2*clearance), (int)bitmapHeight, false);
	}
	private void initBottomSimple() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapHeight = container.height()/5;
		bitmapWidth = bitmapHeight*6;
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.bottom_shadow);
		shadow = Bitmap.createScaledBitmap(temp, (int)(bitmapWidth-2*clearance), (int)bitmapHeight, false);
	}
	private void initSimple() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		shadow = null;
	}
	private void initTopLeft() {
		shape.moveTo(container.left+2*radius, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top+2*radius);
		RectF rect = new RectF(container.left, container.top, container.left+2*radius, container.top+2*radius);
		shape.arcTo(rect, 180.0f, 90.0f);
		shape.close();
		float y = container.top+radius;
		float delta_y = (float)(y - radius*Math.sin(Math.toRadians(45))-container.top); 
		float bitmapWidth, bitmapHeight;
		bitmapWidth = container.width()/5;
		bitmapHeight = bitmapWidth*6-delta_y;
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_shadow);
		shadow = Bitmap.createScaledBitmap(temp, (int)bitmapWidth, (int)(bitmapHeight-clearance*2), false);
	}
	private void initLeftSimple() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left, container.bottom);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapWidth = container.width()/5;
		bitmapHeight = bitmapWidth*6;
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_shadow);
		shadow = Bitmap.createScaledBitmap(temp, (int)bitmapWidth, (int)(bitmapHeight-clearance*2), false);
	}
	private void initLeftBottom() {
		shape.moveTo(container.left, container.top);
		shape.lineTo(container.right, container.top);
		shape.lineTo(container.right, container.bottom);
		shape.lineTo(container.left+radius, container.bottom);
		shape.arcTo(new RectF(container.left, container.bottom-2*radius, container.left+2*radius, container.bottom), 90.0f, 90.0f);
		shape.lineTo(container.left, container.top);
		shape.close();
		float bitmapWidth, bitmapHeight;
		bitmapWidth = 6*container.width()/5;
		bitmapHeight = bitmapWidth;
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_bottom);
		shadow = Bitmap.createScaledBitmap(temp, (int)(bitmapWidth-clearance*2), (int)(bitmapHeight+clearance*2), false);
	}
	public void drawButton(Canvas canvas) {
		Bitmap temp, scaled;
		Paint bitmapPaint = new Paint();
		bitmapPaint.setAntiAlias(true);
		bitmapPaint.setAlpha(0x77);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		PointF center = new PointF();
		switch(type) {
		case TOP_LEFT:
			if (!pressed) {
				float shadow_x, shadow_y;
				center.set(container.left+radius, container.top+radius);
				shadow_x = (float)(center.x - radius*Math.cos(Math.toRadians(45)));
				shadow_y = (float)(center.y - radius*Math.sin(Math.toRadians(45))); 
				canvas.drawBitmap(shadow, shadow_x-shadow.getWidth(), shadow_y, bitmapPaint);
			}
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				innerShadowPaint.setAntiAlias(true);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_first);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()/5), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.right-innerShadow.getWidth(), container.top, innerShadowPaint);
			}
			break;
		case TOP_SIMPLE:
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_first);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()/5), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.right-innerShadow.getWidth(), container.top, innerShadowPaint);
			}
			break;
		case TOP_RIGHT:
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
			}
			break;
		case LEFT_SIMPLE:
			if (!pressed)
				canvas.drawBitmap(shadow, container.left-shadow.getWidth(), container.top+clearance, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_second);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		case SIMPLE:
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_second);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		case RIGHT_SIMPLE:
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_third);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()), (int)(container.height()/5), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		case LEFT_BOTTOM:
			if (!pressed)
				canvas.drawBitmap(shadow, container.right-shadow.getWidth()-clearance, container.top-clearance, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_second);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		case BOTTOM_SIMPLE:
			if (!pressed)
				canvas.drawBitmap(shadow, container.left-shadow.getHeight()+clearance, container.bottom, bitmapPaint);
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_second);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)(container.width()), (int)container.height(), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		case BOTTOM_RIGHT:
			if (!pressed) {
				center.set(container.right-radius, container.bottom-radius);
				float delta_x, delta_y;
				delta_x = (float) (radius*Math.sin(Math.toRadians(45)));
				delta_y = (float) (radius*Math.cos(Math.toRadians(45)));
				canvas.drawBitmap(shadow, center.x+delta_x-shadow.getWidth(), center.y+delta_y, bitmapPaint);
			}
			canvas.drawPath(shape, paint);
			if (pressed) {
				Paint innerShadowPaint = new Paint();
				innerShadowPaint.setColor(Color.BLACK);
				innerShadowPaint.setAlpha(0x22);
				canvas.drawPath(shape, innerShadowPaint);
				Bitmap inTemp = BitmapFactory.decodeResource(context.getResources(), R.drawable.record_pressed_third);
				Bitmap innerShadow = Bitmap.createScaledBitmap(inTemp, (int)container.width(), (int)(container.height()/5), false);
				innerShadowPaint.setAlpha(0x66);
				canvas.drawBitmap(innerShadow, container.left, container.top, innerShadowPaint);
			}
			break;
		}
		bitmapPaint.setAlpha(0xFF);
		if (category != null) {
			int resId = context.getResources().getIdentifier(category.getIcon(), "drawable", context.getPackageName());
			temp = BitmapFactory.decodeResource(context.getResources(), resId);
			scaled = Bitmap.createScaledBitmap(temp, (int)context.getResources().getDimension(R.dimen.thirty_dp), (int)context.getResources().getDimension(R.dimen.thirty_dp), true);
			canvas.drawBitmap(scaled, container.centerX()-scaled.getWidth()/2, container.centerY()-scaled.getHeight(), bitmapPaint);
			Paint textPaint = new Paint();
			textPaint.setColor(ContextCompat.getColor(context, R.color.toolbar_text_color));
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
			textPaint.setAntiAlias(true);
			Rect bounds = new Rect();
			String catText = category.getName();
			for (int i=0; i < category.getName().length(); i++) {
				textPaint.getTextBounds(category.getName(), 0, i, bounds);
				if (bounds.width() >= container.width()) {
					catText = category.getName().substring(0, i-5);
					catText = catText + "...";
					break;
				}
			}
			textPaint.getTextBounds(catText, 0, catText.length(), bounds);
			canvas.drawText(catText, container.centerX()-bounds.width()/2, container.centerY()+2*aLetterHeight, textPaint);
			double amount = PocketAccounterGeneral.calculateAction(category, date);
			if (amount != 0) {
				DecimalFormat format = new DecimalFormat("0.00");
				String text = format.format(amount)+"%";
				bounds = new Rect();
				textPaint.setColor(ContextCompat.getColor(context, R.color.red));
				textPaint.getTextBounds(text, 0, text.length(), bounds);
				canvas.drawText(text, container.centerX()-bounds.width()/2, container.centerY()+4*aLetterHeight, textPaint);
			}
		} else {
			temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_category);
			scaled = Bitmap.createScaledBitmap(temp, (int)context.getResources().getDimension(R.dimen.thirty_dp), (int)context.getResources().getDimension(R.dimen.thirty_dp), true);
			canvas.drawBitmap(scaled, container.centerX()-scaled.getWidth()/2, container.centerY()-scaled.getHeight(), bitmapPaint);
			Paint textPaint = new Paint();
			textPaint.setColor(ContextCompat.getColor(context, R.color.toolbar_text_color));
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.ten_sp));
			Rect bounds = new Rect();
			String text = context.getResources().getString(R.string.add);
			textPaint.setAntiAlias(true);
			bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);
			canvas.drawText(text, container.centerX()-bounds.width()/2, container.centerY()+2*aLetterHeight, textPaint);
		}
	}
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	public RectF getContainer() {
		return container;
	}
	public void setCategory(RootCategory category) {this.category = category;}
}
