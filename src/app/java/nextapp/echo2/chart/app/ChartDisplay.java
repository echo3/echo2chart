package nextapp.echo2.chart.app;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.Plot;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;

/**
 * A component which displays a <code>JFreeChart</code>.
 */
public class ChartDisplay extends Component {

    public static final String CHART_CHANGED_PROPERTY = "chart";
    public static final String CHART_CONTENT_CHANGED_PROPERTY = "chartContent";
    public static final String PROPERTY_HEIGHT = "height";
    public static final String PROPERTY_WIDTH = "width";
    
    /**
     * The displayed <code>JFreeChart</code>.
     */
    private JFreeChart chart;
    
    /**
     * <code>ChartChangeListener</code> to monitor the <code>JFreeChart</code>
     * for changes such that the display will be updated to reflect them.
     */
    private ChartChangeListener chartChangeListener = new ChartChangeListener() {
    
        /**
         * @see org.jfree.chart.event.ChartChangeListener#chartChanged(org.jfree.chart.event.ChartChangeEvent)
         */
        public void chartChanged(ChartChangeEvent e) {
            firePropertyChange(CHART_CONTENT_CHANGED_PROPERTY, null, null);
        }
    };
    
    /**
     * Creates a new empty <code>ChartDisplay</code> that is initially not
     * displaying a <code>JFreeChart</code>.
     */
    public ChartDisplay() {
        super();
    }
    
    /**
     * Creates a new <code>ChartDisplay</code> component that is initially displaying
     * a specific <code>JFreeChart</code>.
     */
    public ChartDisplay(JFreeChart chart) {
        super();
        setChart(chart);
    }

    /**
     * Creates a new <code>ChartDisplay</code> component that is initially displaying
     * a new <code>JFreeChart</code> displaying the specified <code>Plot</code>.
     */
    public ChartDisplay(Plot plot) {
        this(new JFreeChart(plot));
    }
    
    /**
     * Returns the displayed <code>JFreeChart</code>.
     * 
     * @return the displayed <code>JFreeChart</code> 
     */
    public JFreeChart getChart() {
        return chart;
    }
    
    /**
     * Returns the height of the chart.
     * Units must be in pixels values.
     * 
     * @return the height
     */
    public Extent getHeight() {
        return (Extent) getProperty(PROPERTY_HEIGHT);
    }
    
    /**
     * Returns the width of the chart.
     * Units must be in pixels values.
     * 
     * @return the width
     */
    public Extent getWidth() {
        return (Extent) getProperty(PROPERTY_WIDTH);
    }
    
    /**
     * Sets the displayed <code>JFreeChart</code>.
     * 
     * param newValue the new <code>JFreeChart</code> to display 
     */
    public void setChart(JFreeChart newValue) {
        JFreeChart oldValue = chart;
        chart = newValue;
        if (oldValue != null) {
            oldValue.removeChangeListener(chartChangeListener);
        }
        if (newValue != null) {
            newValue.addChangeListener(chartChangeListener);
        }
        firePropertyChange(CHART_CHANGED_PROPERTY, oldValue, newValue);
    }
    
    /**
     * Sets the height of the chart.
     * Units must be in pixels values.
     * 
     * @param newValue the new height
     */
    public void setHeight(Extent newValue) {
        setProperty(PROPERTY_HEIGHT, newValue);
    }
    
    /**
     * Sets the width of the chart.
     * Units must be in pixels values.
     * 
     * @param newValue the new width
     */
    public void setWidth(Extent newValue) {
        setProperty(PROPERTY_WIDTH, newValue);
    }
}
