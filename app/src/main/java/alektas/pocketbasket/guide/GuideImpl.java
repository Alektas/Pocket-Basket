package alektas.pocketbasket.guide;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GuideImpl implements Guide, GuideCase.CaseListener {
    private static final String TAG = "GuideImpl";
    private List<GuideCase> mCases;
    private GuideListener mListener;
    private int mCurrentCaseNumb;
    private GuideCase mCurrentCase;
    private boolean isStarted = false;

    public interface GuideListener {
        void onGuideCaseStart(String caseKey);
        void onGuideStart();
        void onGuideFinish();
    }

    public GuideImpl() {
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
            nextCase();
        }
    }

    /**
     * Inform the guide that the case has happened
     * @param caseKey name of the case that is happened
     */
    @Override
    public void onCaseHappened(String caseKey) {
        if (isGuideStarted() && caseKey.equals(currentCaseKey())) {
            nextCase();
        }
    }

    private GuideCase getCase(String key) {
        for (GuideCase guideCase : mCases) {
            if (key.equals(guideCase.getKey())) return guideCase;
        }
        return null;
    }

    @Override
    public GuideImpl addCase(GuideCase guideCase) {
        guideCase.setCaseListener(this);
        mCases.add(guideCase);
        return this;
    }

    @Override
    public String currentCaseKey() {
        return mCases.get(mCurrentCaseNumb).getKey();
    }

    @Override
    public void setCase(String caseKey) {
        for(int i = 0; i < mCases.size(); i++) {
            GuideCase gc = mCases.get(i);
            if (gc.getKey().equals(caseKey)) {
                mCurrentCase = gc;
                mCurrentCaseNumb = i;
            }
        }

        if (mCurrentCase == null) { Log.e("GUIDE_EXCEPTION",
                "setCase: Guide case with name '" + caseKey + "' doesn't exist."); }
    }

    /**
     * Get case position number in list of all cases.
     * @param caseKey case unique key, should be hold in the GuideContract
     * @return case position
     */
    @Override
    public int caseNumb(String caseKey) {
        for (int i = 0; i < mCases.size(); i++) {
            if (caseKey.equals(mCases.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isGuideStarted() {
        return isStarted;
    }

    @Override
    public void startGuide() {
        mListener.onGuideStart();

        if (mCurrentCaseNumb != 0) {
            mCases.get(mCurrentCaseNumb).hide();
            mCurrentCaseNumb = 0;
        }
        try {
            mCases.get(0).show();
            isStarted = true;
        } catch (NullPointerException e) {
            Log.e(TAG, "to startGuide guide you must add at least one guide case. ", e);
        }
    }

    @Override
    public void nextCase() {
        if (mCurrentCaseNumb != mCases.size() - 1) {
            mCases.get(mCurrentCaseNumb).hide();
            ++mCurrentCaseNumb;
            mCases.get(mCurrentCaseNumb).show();
        } else {
            finishGuide();
        }
    }

    @Override
    public void startFrom(String caseKey) {
        isStarted = true;
        setCase(caseKey);

        if (mCurrentCase != null) { mCurrentCase.show(); }
    }

    @Override
    public void finishGuide() {
        mCases.get(mCurrentCaseNumb).hide();
        mCurrentCaseNumb = 0;
        isStarted = false;
        mListener.onGuideFinish();
    }

    public void setGuideListener(GuideListener listener) {
        mListener = listener;
    }

}