package alektas.pocketbasket.guide.domain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import alektas.pocketbasket.guide.GuideObserver;

public class SequentialGuide implements Guide {
    private static SequentialGuide INSTANCE;
    private List<GuideObserver> mListeners;
    private List<GuideCase> mCases;
    private GuideCase mCurrentCase;
    private boolean isStarted = false;

    private SequentialGuide() {
        mCases = new ArrayList<>();
        mListeners = new ArrayList<>();
    }

    public static Guide getInstance() {
        if (INSTANCE == null) {
            synchronized (SequentialGuide.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SequentialGuide();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Guide addCase(GuideCase guideCase) {
        mCases.add(guideCase);
        return this;
    }

    @Override
    public void onUserEvent(String caseKey) {
        if (!isStarted) return;
        if (caseKey.equals(mCurrentCase.getKey())) {
            nextCase();
        }
    }

    private void nextCase() {
        // The guide is not started yet, so start the first case
        if (mCurrentCase == null) {
            startCase(0);
            return;
        }
        int i = mCases.indexOf(mCurrentCase);
        if (i != mCases.size() - 1) {
            startCase(i + 1);
            return;
        }
        // It was the last case, so finish the guide
        finish();
    }

    @Override
    public void start() {
        isStarted = true;
        notifyGuideStarted();
        startCase(0); // start from the first case in the list
    }

    private void startCase(int index) {
        mCurrentCase = mCases.get(index);
        notifyGuideCaseStarted(mCurrentCase.getKey());
    }

    @Override
    public void startFrom(String caseKey) {
        isStarted = true;
        notifyGuideStarted();
        startCase(caseKey);
    }

    private void startCase(String key) {
        mCurrentCase = getCase(key);
        notifyGuideCaseStarted(key);
    }

    @Nullable
    private GuideCase getCase(String key) {
        for (GuideCase guideCase : mCases) {
            if (key.equals(guideCase.getKey())) return guideCase;
        }
        System.out.println("GUIDE_EXCEPTION: " +
                "getCase: Guide case with name '" + key + "' doesn't exist.");
        return null;
    }

    @Override
    public void finish() {
        mCurrentCase = null;
        isStarted = false;
        notifyGuideFinished();
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public String currentCase() {
        return mCurrentCase == null ? null : mCurrentCase.getKey();
    }

    @Override
    public void observe(GuideObserver listener) {
        mListeners.add(listener);
        if (isStarted) listener.onGuideStart();
        if (mCurrentCase != null) listener.onGuideCaseStart(mCurrentCase.getKey());
    }

    @Override
    public void removeObserver(GuideObserver listener) {
        mListeners.remove(listener);
    }

    private void notifyGuideStarted() {
        for(GuideObserver listener : mListeners) {
            listener.onGuideStart();
        }
    }

    private void notifyGuideFinished() {
        for(GuideObserver listener : mListeners) {
            listener.onGuideFinish();
        }
    }

    private void notifyGuideCaseStarted(String caseKey) {
        for(GuideObserver listener : mListeners) {
            listener.onGuideCaseStart(caseKey);
        }
    }

    private void notifyGuideCaseFinished(String caseKey) {
        for(GuideObserver listener : mListeners) {
            listener.onGuideCaseComplete(caseKey);
        }
    }

}
