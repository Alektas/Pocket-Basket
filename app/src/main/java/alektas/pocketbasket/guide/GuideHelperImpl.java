package alektas.pocketbasket.guide;

public class GuideHelperImpl implements GuideHelper {
    public static final String GUIDE_CHANGE_MODE = "change_mode_case";
    public static final String GUIDE_ADD_ITEM = "add_item_case";
    public static final String GUIDE_CHECK_ITEM = "check_item_case";
    public static final String GUIDE_MOVE_ITEM = "move_item_case";
    public static final String GUIDE_REMOVE_ITEM = "remove_item_case";
    public static final String GUIDE_FINISH = "finish_case";
    public static final String GUIDE_DEL_ITEMS = "delete_items_case";
    public static final String GUIDE_DEL_MODE = "delete_mode_case";
    public static final String GUIDE_FLOATING_MENU = "floating_menu_case";
    public static final String GUIDE_FLOATING_MENU_HELP = "floating_menu_help_case";
    public static final String GUIDE_BASKET_HELP = "basket_help_case";
    public static final String GUIDE_SHOWCASE_HELP = "showcase_help_case";
    public static final String GUIDE_CATEGORIES_HELP = "categories_help_case";
    private Guide mGuide;

    public GuideHelperImpl(Guide guide) {
        mGuide = guide;
    }

    @Override
    public String currentCase() {
        return mGuide.getCurrentCaseKey();
    }

    @Override
    public void startGuide() {
        mGuide.start();
    }

    @Override
    public void nextCase() {
        mGuide.next();
    }

    @Override
    public void finishGuide() {
        mGuide.finish();
    }
}
