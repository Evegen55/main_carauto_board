package entities;

/**
 * @author (created on 11/1/2017).
 */
public class WebCamInfo {

    public final String webCamName;
    public final int webCamIndex;

    public WebCamInfo(final int webCamCounter, final String name) {
        this.webCamIndex = webCamCounter;
        this.webCamName = name;
    }

    @Override
    public String toString() {
        return webCamName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebCamInfo that = (WebCamInfo) o;

        if (webCamIndex != that.webCamIndex) return false;
        return webCamName != null ? webCamName.equals(that.webCamName) : that.webCamName == null;
    }

    @Override
    public int hashCode() {
        int result = webCamName != null ? webCamName.hashCode() : 0;
        result = 31 * result + webCamIndex;
        return result;
    }
}
