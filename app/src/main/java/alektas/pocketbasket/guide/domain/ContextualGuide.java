package alektas.pocketbasket.guide.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nullable;

import alektas.pocketbasket.guide.GuideObserver;

public class ContextualGuide implements Guide {
    private static ContextualGuide INSTANCE;
    private List<GuideObserver> mListeners;
    private List<GuideCase> mCases;
    private Queue<GuideCase> mShowingQueue;
    private GuideCase mCurrentCase;
    private boolean isStarted;

    private ContextualGuide() {
        mShowingQueue = new LinkedList<>();
        mCases = new ArrayList<>();
        mListeners = new ArrayList<>();
    }

    /**
     * Create and get access to the instance of the Guide for the all Application scope.
     * Warning! To fill the Guide by cases use {@link ContextualGuide.Builder}.
     *
     * @return Guide model
     */
    public static Guide getInstance() {
        if (INSTANCE == null) {
            synchronized (ContextualGuide.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ContextualGuide();
                }
            }
        }
        return INSTANCE;
    }

    public static class Builder {
        private List<GuideCase> cases;
        private Map<GuideCase, Requirement> requirementsCases;
        private GuideCase customCase;

        public Builder() {
            cases = new ArrayList<>();
            requirementsCases = new HashMap<>();
        }

        /**
         * Add a guide case to the Guide model. The last added case is still accessed for
         * customizing in the {@link Builder#require(Requirement)} and
         * {@link Builder#showAfter(GuideCase)} methods.
         *
         * @param guideCase guide case model
         * @return Build instance for the chain building.
         */
        public Builder addCase(GuideCase guideCase) {
            if (guideCase == null) throw new NullPointerException("No guide case in the argument");
            cases.add(guideCase);
            customCase = guideCase;
            return this;
        }

        /**
         * Set to the currently added guide case a requirement. If requirement is not met the guide
         * case won't be shown.
         * Require: {@link Builder#addCase(GuideCase)} before this method invoking.
         *
         * @param req requirement which needed to show the guide case
         * @return Build instance for the chain building.
         */
        public Builder require(Requirement req) {
            if (customCase == null) throw
                    new NullPointerException("No guide case added before setting the requirement");
            requirementsCases.put(customCase, req);
            return this;
        }

        /**
         * Show the currently added guide case right after the {@code previousCase}.
         * Warning! If {@code previousCase} still not added, this method won't work!
         * Require: {@link Builder#addCase(GuideCase)} with {@code previousCase} argument.
         *
         * @param previousCase the guide case after which the currently added case will be shown.
         * @return  Build instance for the chain building.
         */
        public Builder showAfter(GuideCase previousCase) {
            if (previousCase == null) throw new NullPointerException("Previous guide case is null");
            if (customCase == null) throw new NullPointerException("No guide case added before");
            previousCase.linkCase(customCase);
            return this;
        }

        public Guide build() {
            ContextualGuide guide = (ContextualGuide) getInstance();
            if (guide.isStarted()) guide.finish();
            guide.mCases = cases;
            guide.mShowingQueue.clear();
            for (GuideCase guideCase : cases) {
                Requirement req = requirementsCases.get(guideCase);
                if (req == null) {
                    // If no requirements add a case to the showing queue without check
                    guide.addCase(guideCase);
                    continue;
                }
                req.observe((isMet -> {
                    if (guideCase.isCompleted()) {
                        // Requirements shouldn't be observed for the completed cases
                        req.removeObserver();
                        return;
                    }
                    guide.onRequirementChange(guideCase, isMet);
                }));
                // Add to showing queue only cases which requirements are met
                if (req.check()) guide.addCase(guideCase);
            }
            return guide;
        }
    }

    private void onRequirementChange(GuideCase guideCase, boolean isMet) {
        if (!isStarted || guideCase.isCompleted()) return;
        if (isMet) {
            addCase(guideCase);
            GuideCase nextCase = guideCase.getLinkedCase();
            if (nextCase != null) addCase(nextCase);
            if (mCurrentCase == null) startNextCase();
        } else {
            mShowingQueue.remove(guideCase);
            GuideCase nextCase = guideCase.getLinkedCase();
            if (nextCase != null) mShowingQueue.remove(nextCase);
            if (guideCase.equals(mCurrentCase)) startNextCase();
        }
    }

    @Override
    public void onUserEvent(String caseKey) {
        if (!isStarted) return;
        if (mCurrentCase != null && caseKey.equals(mCurrentCase.getKey())) {
            completeCurrentCase();
            startNextCase();
            return;
        }
        completeCase(caseKey);
    }

    /**
     * Stop the current guide case with completing
     */
    private void completeCurrentCase() {
        mCurrentCase.complete();
        mShowingQueue.poll();
        notifyGuideCaseComplete(mCurrentCase.getKey());
        hideCurrentCase();
    }

    /**
     * Start next in the queue guide case without completing the previous.
     * If no cases in the queue, hide previous case only.
     */
    private void startNextCase() {
        GuideCase guideCase = mShowingQueue.peek();
        if (guideCase == null) {
            hideCurrentCase();
            return;
        }
        startCase(guideCase);
    }

    private void startCase(GuideCase guideCase) {
        if (guideCase == null) return;
        mCurrentCase = guideCase;
        notifyGuideCaseStarted(mCurrentCase.getKey());
    }

    @Override
    public void start() {
        isStarted = true;
        notifyGuideStarted();
        startNextCase();
    }

    /**
     * Stop a guide case with completing
     *
     * @param caseKey key of the guide case which should be completed
     */
    public void completeCase(String caseKey) {
        if (!isStarted) return;
        GuideCase guideCase = getCase(caseKey);
        if (guideCase != null) {
            guideCase.complete();
            mShowingQueue.remove(guideCase);
            notifyGuideCaseComplete(caseKey);
            if (guideCase.equals(mCurrentCase)) {
                hideCurrentCase();
                startNextCase();
            }
        }
    }

    private void hideCurrentCase() {
        if (mCurrentCase != null) {
            mCurrentCase = null;
            notifyGuideCaseStarted(null);
        }
    }

    @Nullable
    private GuideCase getCase(String caseKey) {
        for (GuideCase guideCase : mCases) {
            if (guideCase.getKey().equals(caseKey)) return guideCase;
        }
        return null;
    }

    /**
     * Not used in the ContextualGuide
     */
    @Override
    public void startFrom(String caseKey) { }

    /**
     * Add a guide case into queue of the showing cases regardless of the case requirement.
     *
     * @param guideCase guide case which will be added to the showing queue
     * @return guide instance for the chain invoking
     */
    @Override
    public Guide addCase(GuideCase guideCase) {
        if (mShowingQueue.contains(guideCase)) return this;
        mShowingQueue.offer(guideCase);
        return this;
    }

    @Override
    public void finish() {
        if (mCurrentCase != null) {
            mCurrentCase = null;
            notifyGuideCaseStarted(null);
        }
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

    private void notifyGuideCaseComplete(String caseKey) {
        for(GuideObserver listener : mListeners) {
            listener.onGuideCaseComplete(caseKey);
        }
    }

}
