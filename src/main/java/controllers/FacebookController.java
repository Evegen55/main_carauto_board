package controllers;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author (created on 11/21/2017).
 */
public class FacebookController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FacebookController.class);

    private Tab tab;

    private Facebook facebook;

    public FacebookController(final Tab tab_with_facebook) {
        tab = tab_with_facebook;
    }

    public void initLoginFlow() {
        LOGGER.info("Start initialization for a login page for facebook");
        facebook = new FacebookFactory().getInstance();

//        facebook.setOAuthAppId(appId, appSecret);
//        facebook.setOAuthPermissions(commaSeparetedPermissions);
//        facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
    }
}
