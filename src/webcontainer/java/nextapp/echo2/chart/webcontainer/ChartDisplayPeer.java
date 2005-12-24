package nextapp.echo2.chart.webcontainer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.chart.app.ChartDisplay;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webrender.servermessage.DomUpdate;

/**
 * <code>ComponentSynchronizePeer</code> implementation for 
 * <code>ChartDisplay</code> components.
 */
public class ChartDisplayPeer 
implements ComponentSynchronizePeer, DomUpdateSupport {
    
    /**
     * <code>RenderState</code> implementation (stores generated image 
     * to be rendered).
     */
    private class ChartRenderState 
    implements RenderState {
        
        private int version;
    }
    
    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("ChartDisplay component does not support children.");
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
        
        ChartRenderState renderState = (ChartRenderState) rc.getContainerInstance().getRenderState(chartDisplay);
        if (renderState == null) {
            renderState = new ChartRenderState();
            rc.getContainerInstance().setRenderState(chartDisplay, renderState);
        } else {
            ++renderState.version;
        }

        if (chartDisplay.getChart() != null) {
            Element imgElement = document.createElement("img");
            imgElement.setAttribute("src", ChartImageService.getUri(rc, chartDisplay, renderState.version));
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
