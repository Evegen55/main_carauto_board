package entities;

/**
 * @author (created on 11/1/2017).
 */
public class WebCamInfo {

    private String webCamName;
    private int webCamIndex;

    public String getWebCamName() {
        return webCamName;
    }

    public void setWebCamName(String webCamName) {
        this.webCamName = webCamName;
    }

    public int getWebCamIndex() {
        return webCamIndex;
    }

    public void setWebCamIndex(int webCamIndex) {
        this.webCamIndex = webCamIndex;
    }

    @Override
    public String toString() {
        return webCamName;
    }
}
