package nextapp.echo2.chart.webcontainer;

import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import nextapp.echo2.app.AwtImageReference;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.chart.app.ChartDisplay;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.image.ImageTools;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.servermessage.DomUpdate;

/**
 * <code>ComponentSynchronizePeer</code> implementation for 
 * <code>ChartDisplay</code> components.
 */
public class ChartDisplayPeer 
implements ComponentSynchronizePeer, DomUpdateSupport, ImageRenderSupport {
    
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 300;
    
    /**
     * <code>RenderState</code> implementation (stores generated image 
     * to be rendered).
     */
    private class ChartRenderState 
    implements RenderState {
        
        /**
         * The rendered chart image.
         */
        private ImageReference image;
    }
    
    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("ChartDisplay component does not support children.");
    }

    /**
     * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component, java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if ("chart".equals(imageId)) {
            ContainerInstance containerInstance = (ContainerInstance) WebRenderServlet.getActiveConnection().getUserInstance();
            ChartRenderState renderState = (ChartRenderState) containerInstance.getRenderState(component);
            return renderState == null ? null : renderState.image;
        }
        return null;
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
     *      nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAdd(rc.getServerMessage(), targetId, htmlFragment);
    }
    
    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate,
     *      nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        // Do nothing.
    }

    /**
     * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node, nextapp.echo2.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        ChartDisplay chartDisplay = (ChartDisplay) component;
        Document document = rc.getServerMessage().getDocument();
        String elementId = ContainerInstance.getElementId(chartDisplay);
        
        Element divElement = document.createElement("div");
        divElement.setAttribute("id", elementId);
        
        ChartRenderState renderState = new ChartRenderState();
        JFreeChart chart =  chartDisplay.getChart();
        int width = ExtentRender.toPixels((Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_WIDTH), DEFAULT_WIDTH);
        int height = ExtentRender.toPixels((Extent) chartDisplay.getRenderProperty(ChartDisplay.PROPERTY_HEIGHT), DEFAULT_HEIGHT);
        renderState.image = new AwtImageReference(chart.createBufferedImage(width, height));
        rc.getContainerInstance().setRenderState(chartDisplay, renderState);
        
        if (chartDisplay.getChart() != null) {
            Element imgElement = document.createElement("img");
            imgElement.setAttribute("src", ImageTools.getUri(rc, this, chartDisplay, "chart"));
            divElement.appendChild(imgElement);
        }

        parentNode.appendChild(divElement);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return true;
    }
}
