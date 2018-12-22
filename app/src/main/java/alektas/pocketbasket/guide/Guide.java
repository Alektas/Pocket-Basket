package alektas.pocketbasket.guide;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Guide implements GuideCase.CaseListener {
    private static final String TAG = "Guide";
    private List<GuideCase> mCases;
    private int mCurrentCaseNumb;
    private GuideListener mListener;

    public interface GuideListener {
        void onGuideCaseStart(String caseKey);
        void onGuideFinish();
    }

    public Guide() {
        mCases = new ArrayList<>();
    }

    @Override
    public void onCaseStart(String caseKey) {
        mListener.onGuideCaseStart(caseKey);
    }

    @Override
    public void onCaseFinish(String caseKey) {
        GuideCase guideCase = getCase(caseKey);
        if (guideCase != null && guideCase.isAutoNext()) {
            next();
        }
    }

    private GuideCase getCase(String key) {
        for (GuideCase guideCase : mCases) {
            if (key.equals(guideCase.getKey())) return guideCase;
        }
        return null;
    }

    public Guide addCase(GuideCase guideCase) {
        guideCase.setCaseListener(this);
        mCases.add(guideCase);
        return this;
    }

    public String getCurrentCaseKey() {
        return mCases.get(mCurrentCaseNumb).getKey();
    }

    public void start() {
        if (mCurrentCaseNumb != 0) {
            mCases.get(mCurrentCaseNumb).hide();
            mCurrentCaseNumb = 0;
        }
        try {
            mCases.get(0).show();
        } catch (NullPointerException e) {
            Log.e(TAG, "to start guide you must add at least one guide case. ", e);
        }
    }

    public void next() {
        if (mCurrentCaseNumb != mCases.size() - 1) {
            mCases.get(mCurrentCaseNumb).hide();
            ++mCurrentCaseNumb;
            mCases.get(mCurrentCaseNumb).show();
        } else {
            finish();
        }
    }

    public void finish() {
        mCases.get(mCurrentCaseNumb).hide();
        mCurrentCaseNumb = 0;
        mListener.onGuideFinish();
    }

    public void setGuideListener(GuideListener listener) {
        mListener = listener;
    }

    public void removeGuideListener() {
        mListener = null;
    }
}
