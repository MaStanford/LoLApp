package com.stanford.lolapp.network;

/**
 * Contains all the requests for the LoL app. Just a holder for them
 *
 * Created by Mark Stanford on 5/21/14.
 */
public class Requests {

    private Requests(){

    }

    /**
     * If I ever decide to have users and need to log in
     */
    public static class Login extends LoLAppWebserviceRequest {
        public static final String PARAM_REQUIRED_USERNAME = "username";
        public static final String PARAM_REQUIRED_PASSWORD = "password";

        public Login() {
            super(WebService.SERVICE_LOGIN, WebService.PATH_LOGIN);
            mRequiredParams.add(PARAM_REQUIRED_USERNAME);
            mRequiredParams.add(PARAM_REQUIRED_PASSWORD);
            mRequiredHeaders.add(WebService.HEADER_REQUIRED_API_KEY);
            mRequiredHeaders.add(WebService.HEADER_REQUIRED_APP_KEY);
        }
    }

    /**
     * If I ever decide to make users
     */
    public static class CreateUser extends LoLAppWebserviceRequest {

        public static final String PARAM_REQUIRED_USERNAME  = "username";
        public static final String PARAM_REQUIRED_PASSWORD  = "password";

        public static final String PARAM_OPTIONAL_EMAIL     = "email";

        public CreateUser() {
            super(WebService.SERVICE_SIGN_UP, WebService.PATH_CREATE_USER);
            mRequiredBody.add(PARAM_REQUIRED_USERNAME);
            mRequiredBody.add(PARAM_REQUIRED_PASSWORD);
            mOptionalBody.add(PARAM_OPTIONAL_EMAIL);
            mRequiredHeaders.add(WebService.HEADER_REQUIRED_API_KEY);
            mRequiredHeaders.add(WebService.HEADER_REQUIRED_APP_KEY);
        }
    }

    /**
     * Get a list of Champion IDs
     */
    public static class GetAllChampionIds extends LoLAppWebserviceRequest {

        public GetAllChampionIds() {
            super(WebService.SERVICE_GET_ALL_CHAMPION_IDS, WebService.PATH_GET_ALL_CHAMP_IDS);
        }
    }

    /**
     * Get a single champion ID
     */
    public static class GetChampionId extends LoLAppWebserviceRequest {


        public GetChampionId(String championID) {
            super(WebService.SERVICE_GET_CHAMPION_ID, WebService.PATH_GET_CHAMP_ID + championID);
        }
    }

    /**
     * Get a champion
     */
    public static class GetChampionData extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData"; //Use Enum for this


        public GetChampionData(int championID) {
            super(WebService.SERVICE_GET_CHAMPION_DATA, WebService.PATH_GET_CHAMP + championID);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    /**
     * Get a list of all the champions
     */
    public static class GetAllChampionData extends LoLAppWebserviceRequest {

        public static final String PARAM_DATA = "champData"; //Use Enum for this

        //Enum for ChampData parameter
        public enum ChampData {
            all("all"),
            allytips("allytips"),
            altimages("altimages"),
            blurb("blurb"),
            enemytips("enemytips"),
            image("image"),
            info("info"),
            lore("lore"),
            partype("partype"),
            passive("passive"),
            recommended("recommended"),
            skins("skins"),
            spells("spells"),
            stats("stats"),
            tags("tags");
            private String mData = "";
            ChampData(String data) {
                this.mData = data;
            }
            public String getData() {
                return this.mData;
            }
        }

        public GetAllChampionData() {
            super(WebService.SERVICE_GET_ALL_CHAMPION_DATA, WebService.PATH_GET_ALL_CHAMPS);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static class GetItem extends LoLAppWebserviceRequest{

        public static final String PARAM_DATA = "itemData"; //Use Enum for this

        public GetItem(int itemID){
            super(WebService.SERVICE_GET_ALL_ITEM_DATA, WebService.PATH_GET_ITEM + itemID);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }

    public static class GetAllItems extends LoLAppWebserviceRequest{

        public static final String PARAM_DATA = "itemListData"; //Use Enum for this

        //Enum for item data parameter
        public enum ItemData{
            all("all"),
            colloq("colloq"),
            consumeOnFull("consumeOnFull"),
            consumed("consumed"),
            depth("depth"),
            from("from"),
            gold("gold"),
            groups("groups"),
            hideFromAll("hideFromAll"),
            image("image"),
            inStore("inStore"),
            into("into"),
            maps("maps"),
            requiredChampion("requiredChampion"),
            sanitizedDescription("sanitizedDescription"),
            specialRecipe("specialRecipe"),
            stacks("stacks"),
            stats("stats"),
            tags("tags"),
            tree("tree");
            private String param;
            ItemData(String param){
                this.param = param;
            }
            public String getParam(){
                return this.param;
            }
        }

        public GetAllItems(){
            super(WebService.SERVICE_GET_ITEM_DATA, WebService.PATH_GET_ALL_ITEMS);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCATION);
            mRequiredParams.add(WebService.PARAM_REQUIRED_LOCALE);
            mOptionalParams.add(PARAM_DATA);
        }
    }
}
