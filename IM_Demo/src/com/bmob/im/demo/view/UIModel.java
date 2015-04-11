package com.bmob.im.demo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;






import com.bmob.im.demo.util.ColorData;
import com.bmob.im.demo.util.RectArea;
import com.deep.momo.game.view.MixedColorView;

import android.R.integer;
import android.graphics.RectF;

public class UIModel {

	public static final int COLOR_TYPE_BLACK = 0;
	public static final int COLOR_TYPE_BHITE = 1;
	public static final int COLOR_TYPE_YELLOW = 2;
	public static final int COLOR_TYPE_RED = 3;
	public static final int COLOR_TYPE_GREEN = 4;
	public static final int COLOR_TYPE_BLUE = 5;
	public static final int COLOR_TYPE_PINK = 6;
	public static final int COLOR_TYPE_PURPLE = 7;
	public static final int COLOR_TYPE_BROWN = 8;


	public static final int TOTAL_COLOR_AMOUNT = 9;


	public static final int FIELD_VIRGIN = 111;
	public static final int FIELD_MARK = 999;

	public static final int GAME_ATTRIBUTE_TOTAL_LEVEL = 6;

	public static final int GAME_ATTRIBUTE_LEAST_COLOR = 4;

	public static final int GAME_ATTRIBUTE_TOTAL_STAGE = 10;

	public static long GAME_ATTRIBUTE_MAX_TIME_PER_STAGE = 3000;

	public static final int GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT = 3;

	public static final int TOTAL_GRID_AMOUNT = GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT
			* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
	public static final int GAME_STATUS_PAUSE = 0;
	public static final int GAME_STATUS_RUNNING = 1;
	public static final int GAME_STATUS_GAMEOVER = 2;

	public static final int EFFECT_FLAG_NO_EFFECT = 0;
	public static final int EFFECT_FLAG_PASS = 1;
	public static final int EFFECT_FLAG_TIMEOUT = 2;
	public static final int EFFECT_FLAG_MISS = 3;


	public static final int UI_ATTRIBUTE_TARGET_CELL_MARGIN = 2;
	public static final int UI_ATTRIBUTE_SOURCE_CELL_X_MARGIN = 25;
	public static final int UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP = 3;
	public static final int UI_ATTRIBUTE_INNER_PADDING_Y = 7;

	private Random mRan = new Random();

	private int mGameStatus;

	private RectArea mCanvasArea;

	private RectArea mSrcPaintArea;

	private RectArea mTarPaintArea;

	private RectArea mSrcGrid;

	private RectArea[] mTarGrid;

	private ColorData mSrcColor;

	private List<ColorData> mTarColor = new ArrayList<ColorData>();

	private int mEffectFlag;

	private int mStageCounter;

	private long mTimeLogger;

	private long mStageTime;

	private long mTotalTime;
	
	public synchronized void updateUIModel() {
		long curTimeMillis = System.currentTimeMillis();
		mStageTime += curTimeMillis - mTimeLogger;
		mTimeLogger = curTimeMillis;
		if (mStageTime >= GAME_ATTRIBUTE_MAX_TIME_PER_STAGE) {
			mEffectFlag = EFFECT_FLAG_TIMEOUT;			
			buildStage();
		}
	}

	public synchronized void buildStage() {
		mTotalTime += (mStageTime < GAME_ATTRIBUTE_MAX_TIME_PER_STAGE) ? mStageTime
				: GAME_ATTRIBUTE_MAX_TIME_PER_STAGE;
		mStageCounter++;
		if (mStageCounter < GAME_ATTRIBUTE_TOTAL_STAGE) {
			buildPaintArea(mStageCounter % GAME_ATTRIBUTE_TOTAL_LEVEL
					+ GAME_ATTRIBUTE_LEAST_COLOR);
			mStageTime = 0;
			mTimeLogger = System.currentTimeMillis();
		} else {
			mGameStatus = GAME_STATUS_GAMEOVER;
		}
	}

	public void initStage() {
		mStageCounter = 0;
		mStageTime = 0;
		mTotalTime = 0;
		mTimeLogger = System.currentTimeMillis();
		buildPaintArea(GAME_ATTRIBUTE_LEAST_COLOR);
	}

	public void buildPaintArea(int colorAmount) {


		int[] selColors = new int[colorAmount];
		for (int i = 0; i < colorAmount; i++) {
			selColors[i] = FIELD_VIRGIN;
		}

		randomMethod(selColors, 0, TOTAL_COLOR_AMOUNT);


		int[] srcPos = new int[colorAmount];
		for (int i = 0; i < colorAmount; i++) {
			srcPos[i] = FIELD_VIRGIN;
		}

		randomMethod(srcPos, 0, TOTAL_GRID_AMOUNT);

		int[] paintPos = new int[TOTAL_GRID_AMOUNT];
		for (int i = 0; i < TOTAL_GRID_AMOUNT; i++) {
			paintPos[i] = FIELD_VIRGIN;
		}
		for (int i = 0; i < colorAmount; i++) {

			paintPos[srcPos[i]] = selColors[i];
		}


		int srcIndex;

		int srcIndexY;

		int srcMinX;

		int srcMaxX;

		int gateIndex;

		int posOffset;

		boolean isDirty;


		for (int i = 0; i < colorAmount; i++) {
			srcIndex = srcPos[i];
			srcIndexY = srcIndex / GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			srcMinX = srcIndex;
			srcMaxX = srcIndex;


			gateIndex = GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT * srcIndexY;
			for (int j = srcMinX - 1; j >= gateIndex; j--) {
				if (paintPos[j] == FIELD_VIRGIN) {
					paintPos[j] = selColors[i];
					srcMinX = j;
				} else {
					break;
				}
			}

			gateIndex = GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT
					* (srcIndexY + 1) - 1;
			for (int j = srcMaxX + 1; j <= gateIndex; j++) {
				if (paintPos[j] == FIELD_VIRGIN) {
					paintPos[j] = selColors[i];
					srcMaxX = j;
				} else {
					break;
				}
			}


			for (int j = srcIndexY - 1; j >= 0; j--) {
				isDirty = false;
				posOffset = (j - srcIndexY)
						* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
				gateIndex = srcMaxX + posOffset;
				for (int k = srcMinX + posOffset; k <= gateIndex; k++) {
					if (paintPos[k] == FIELD_VIRGIN) {
						paintPos[k] = selColors[i];
					} else {
						gateIndex = srcMinX + posOffset;
						for (int l = k - 1; l >= gateIndex; l--) {
							paintPos[l] = FIELD_VIRGIN;
						}
						isDirty = true;
						break;
					}
				}
				if (isDirty) {
					break;
				}
			}

			for (int j = srcIndexY + 1; j <= GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT - 1; j++) {
				isDirty = false;
				posOffset = (j - srcIndexY)
						* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
				gateIndex = srcMaxX + posOffset;
				for (int k = srcMinX + posOffset; k <= gateIndex; k++) {
					if (paintPos[k] == FIELD_VIRGIN) {
						paintPos[k] = selColors[i];
					} else {
						gateIndex = srcMinX + posOffset;
						for (int l = k - 1; l >= gateIndex; l--) {
							paintPos[l] = FIELD_VIRGIN;
						}
						isDirty = true;
						break;
					}
				}
				if (isDirty) {
					break;
				}
			}
		}

		int minItemIndex;
		int maxItemIndex;
		int curColor;
		ColorData curColorData;
		RectArea curRectArea;
		mTarColor.clear();
		for (int i = 0; i < colorAmount; i++) {
			minItemIndex = FIELD_VIRGIN;
			maxItemIndex = FIELD_VIRGIN;
			curColor = selColors[i];
			curColorData = new ColorData();
			curColorData.setMBgColor(curColor);
			curColorData.setMTextColor(selColors[(i + 1) % colorAmount]);
			curColorData.setMText(selColors[(i + 2) % colorAmount]);
			for (int j = 0; j < TOTAL_COLOR_AMOUNT; j++) {
				if (paintPos[j] == curColor) {
					if (minItemIndex == FIELD_VIRGIN) {
						minItemIndex = j;
					}
					maxItemIndex = j;
				}
			}
			curRectArea = mTarGrid[minItemIndex];
			curColorData.mMinX = curRectArea.mMinX;
			curColorData.mMinY = curRectArea.mMinY;
			curRectArea = mTarGrid[maxItemIndex];
			curColorData.mMaxX = curRectArea.mMaxX;
			curColorData.mMaxY = curRectArea.mMaxY;
			mTarColor.add(curColorData);
		}


		int[] srcColor = new int[3];
		for (int i = 0; i < srcColor.length; i++) {
			srcColor[i] = FIELD_VIRGIN;
		}
		randomMethod(srcColor, 0, colorAmount);
		mSrcColor = new ColorData();
		mSrcColor.setMBgColor(selColors[srcColor[0]]);
		mSrcColor.setMTextColor(selColors[srcColor[1]]);
		mSrcColor.setMText(selColors[srcColor[2]]);
		mSrcColor.mMinX = mSrcGrid.mMinX;
		mSrcColor.mMaxX = mSrcGrid.mMaxX;
		mSrcColor.mMinY = mSrcGrid.mMinY;
		mSrcColor.mMaxY = mSrcGrid.mMaxY;
	}

	public void randomMethod(int[] arr, int start, int end) {
		if (start >= end) {
			return;
		}
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == FIELD_VIRGIN) {
				arr[i] = FIELD_MARK;
				int selectedIndex = start + mRan.nextInt(end - start);
				arr[i] = selectedIndex;
				randomMethod(arr, start, selectedIndex);
				randomMethod(arr, selectedIndex + 1, end);
				break;
			}
		}
	}

	public void checkSelection(int x, int y) {
		ColorData checkedData = null;
		for (ColorData curColorData : mTarColor) {
			if (curColorData.mMinX < x && curColorData.mMaxX > x
					&& curColorData.mMinY < y && curColorData.mMaxY > y) {
				checkedData = curColorData;
			}
		}
		if (checkedData != null) {
			if (mSrcColor.getMTextColor() == checkedData.getMText()) {
				mEffectFlag = EFFECT_FLAG_PASS;
				MixedColorView.correct++;
				
						
				buildStage();
			} else {
				mEffectFlag = EFFECT_FLAG_MISS;
				mStageTime += 2000;
			}
		}
	}

	public RectF getSrcPaintArea() {
		return mSrcPaintArea.getRectF();
	}

	public RectF getTarPaintArea() {
		return mTarPaintArea.getRectF();
	}

	public List<ColorData> getTargetColor() {
		return mTarColor;
	}

	public ColorData getSourceColor() {
		return mSrcColor;
	}

	public String getStageText() {
		return (mStageCounter < GAME_ATTRIBUTE_TOTAL_STAGE ? (mStageCounter + 1)
				: mStageCounter)
				+ "/" + GAME_ATTRIBUTE_TOTAL_STAGE;
	}

	public String getTimeText() {
		String decimal = String.valueOf((mStageTime % 1000) * 100 / 1000);
		if (decimal.length() < 2) {
			decimal += "0";
		}
		return mStageTime / 1000 + "." + decimal + "s";
	}

	public float getTimePercent() {
		return 1 - (float) mStageTime / GAME_ATTRIBUTE_MAX_TIME_PER_STAGE;
	}

	public float getFinalRecord() {
		return (float) mTotalTime / (mStageCounter * 1000);
	}

	public int getStatus() {
		return mGameStatus;
	}

	public int getEffectFlag() {
		try {
			return mEffectFlag;
		} finally {
			mEffectFlag = EFFECT_FLAG_NO_EFFECT;
		}
	}

	public UIModel(RectArea canvasArea) {
		mCanvasArea = canvasArea;
		mSrcPaintArea = new RectArea(canvasArea.mMinX, canvasArea.mMinY
				+ UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP, canvasArea.mMaxX,
				canvasArea.mMaxY - canvasArea.mMaxX
						- UI_ATTRIBUTE_INNER_PADDING_Y * 2);

		mTarPaintArea = new RectArea(canvasArea.mMinX, canvasArea.mMaxY
				- canvasArea.mMaxX - UI_ATTRIBUTE_INNER_PADDING_Y,
				canvasArea.mMaxX, canvasArea.mMaxY);

		mTarGrid = new RectArea[TOTAL_GRID_AMOUNT];
		mTarColor = new ArrayList<ColorData>();
		int gridSize = (mCanvasArea.mMaxX - mCanvasArea.mMinX - (GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT + 1)
				* UI_ATTRIBUTE_TARGET_CELL_MARGIN)
				/ GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
		int startX = UI_ATTRIBUTE_TARGET_CELL_MARGIN;
		int startY = mCanvasArea.mMaxY
				- (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
				* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
		int posOffsetX;
		int posOffsetY;
		for (int i = 0; i < TOTAL_GRID_AMOUNT; i++) {
			posOffsetX = i % GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			posOffsetY = i / GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			mTarGrid[i] = new RectArea(
					startX + (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
							* posOffsetX, startY
							+ (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
							* posOffsetY, gridSize);
		}
		mSrcGrid = new RectArea(UI_ATTRIBUTE_SOURCE_CELL_X_MARGIN,
				mSrcPaintArea.mMaxY - UI_ATTRIBUTE_INNER_PADDING_Y - gridSize,
				mCanvasArea.mMaxX - UI_ATTRIBUTE_SOURCE_CELL_X_MARGIN,
				mSrcPaintArea.mMaxY - UI_ATTRIBUTE_INNER_PADDING_Y);
		initStage();
		mGameStatus = GAME_STATUS_RUNNING;
		mEffectFlag = EFFECT_FLAG_NO_EFFECT;
	}
}
