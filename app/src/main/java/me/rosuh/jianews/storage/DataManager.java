package me.rosuh.jianews.storage;

/**
 * @author rosu
 * @date 2018/9/30
 */
public class DataManager {
    private IDataModel mIDataModel;
    private DataManager mDataManager;

    private static class DataManagerHolder{
        private final static DataManager INSTANCE = new DataManager();
    }

    public static DataManager getInstance() {
        return DataManagerHolder.INSTANCE;
    }

    private DataManager(){}


}
