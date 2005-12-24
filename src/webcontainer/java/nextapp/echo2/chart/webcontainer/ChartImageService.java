package nextapp.echo2.chart.webcontainer;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.JFreeChart;

import nextapp.echo2.app.Extent;
import nextapp.echo2.chart.app.ChartDisplay;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.image.PngEncoder;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.ContentType;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;

public class ChartImageService 
implements Service {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 300;
    
    private static final String[] URI_KEYS = {"chartId", "v"}; 
    
    static final String getUri(RenderContext rc, ChartDisplay chartDisplay, int version) {
        return rc.getContainerInstance().getServiceUri(INSTANCE, URI_KEYS, new String[]{chartDisplay.getRenderId(), 
                Integer.toString(version)});
    }
    
    private static final ChartImageService INSTANCE = new ChartImageService();
    static {
        WebRenderServlet.getServiceRegistry().add(INSTANCE);
    }
    
    /** Self-instantiated singleton. */
    private ChartImageService() { }
    
    /**
     * @see nextapp.echo2.webrender.Service#getId()
     */
    public String getId() {
        return "Echo2Chart.ImageService";
    }

    /**
     * @see nextapp.echo2.webrender.Service#getVersion()
     */
    public int getVersion() {
        // Cache-able by unique URI.
        return 0;
    }

    /**
     * @see nextapp.echo2.webrender.Service#service(nextapp.echo2.webrender.Connection)
     */
    public void service(Connection conn) throws IOException {
        ContainerInstance containerInstance = (ContainerInstance) conn.getUserInstance();
        HttpServletRequest request = conn.getRequest();
        String chartId = request.getParameter("chartId");
        ChartDisplay chartDisplay = (ChartDisplay) containerInstance.getApplicationInstance().getComponentByRenderId(chartId);
        if (chartDisplay == null || !chartDisplay.isRenderVisible()) {
            throw new IllegalArgumentException("Invalid chart id.");
        }
        
        int width = ExtentRender.toPixels((Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_WIDTH), DEFAULT_WIDTH);
        int height = ExtentRender.toPixels((Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_HEIGHT), DEFAULT_HEIGHT);
        JFreeChart chart =  chartDisplay.getChart();
        BufferedImage image = chart.createBufferedImage(width, height);
        PngEncoder encoder = new PngEncoder(image, true, null, 3);
        conn.setContentType(ContentType.IMAGE_PNG);
        encoder.encode(conn.getOutputStream());
    }
}
