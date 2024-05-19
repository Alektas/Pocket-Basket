package alektas.pocketbasket.guide.ui;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SequentialGuidePresenter implements GuidePresenter, GuideViewListener {
    private static final String TAG = "GuidePresenter";
    private DisposableGuideCaseListener mListener;
    private final List<GuideCaseView> mCaseViews;
    private GuideCaseView mCurCaseView;

    public SequentialGuidePresenter(DisposableGuideCaseListener listener) {
        mCaseViews = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public GuidePresenter addCase(GuideCaseView guideCase) {
        guideCase.setCaseViewListener(this);
        mCaseViews.add(guideCase);
        return this;
    }

    @Override
    public void showCase(String key) {
        mCurCaseView = getCaseView(key);
        if (mCurCaseView != null) {
            mCurCaseView.show();
        } else {
            Log.d(TAG, "showCase: guide case view with key '" + key + "' doesn't exist.");
        }
    }

    @Override
    public void hideCase(String key) {
        GuideCaseView caseView = getCaseView(key);
        if (caseView != null) {
            caseView.hide();
        } else {
            Log.d(TAG, "hideCase: guide case view with key '" + key + "' doesn't exist.");
        }
    }

    @Override
    public void hideCurrentCase() {
        if (mCurCaseView != null) {
            mCurCaseView.hide();
            mCurCaseView = null;
        } else {
            Log.d(TAG, "hideCurrentCase: no one guide case is showing.");
        }
    }

    private GuideCaseView getCaseView(String key) {
        for (GuideCaseView caseView : mCaseViews) {
            if (caseView.getKey().equals(key)) return caseView;
        }
        return null;
    }

    @Override
    public void onCaseStart(String caseKey) { }

    @Override
    public void onCaseFinish(String caseKey) {
        GuideCaseView view = getCaseView(caseKey);
        if (view == null) return;
        if (view.isDisposable()) {
            mListener.onDisposableCaseFinish(caseKey);
        }
    }
}
