package com.GEProspect;

import net.runelite.client.ui.ColorScheme;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PriceChartPanel extends JPanel {
    private static final NumberFormat GP_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    private final TimeSeries buyPriceSeries;
    private final TimeSeries sellPriceSeries;
    private final ChartPanel chartPanel;
    
    public PriceChartPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        // Create time series for buy and sell prices
        buyPriceSeries = new TimeSeries("Buy Price");
        sellPriceSeries = new TimeSeries("Sell Price");
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buyPriceSeries);
        dataset.addSeries(sellPriceSeries);
        
        // Create chart
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            null,           // No title
            "Time",        // x-axis label
            "Price",       // y-axis label
            dataset,       // data
            true,         // include legend
            true,         // tooltips
            false         // urls
        );
        
        // Customize chart appearance
        chart.setBackgroundPaint(ColorScheme.DARKER_GRAY_COLOR);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(ColorScheme.DARKER_GRAY_COLOR);
        plot.setDomainGridlinePaint(ColorScheme.MEDIUM_GRAY_COLOR);
        plot.setRangeGridlinePaint(ColorScheme.MEDIUM_GRAY_COLOR);
        
        // Customize date axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(TIME_FORMAT);
        axis.setLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        axis.setTickLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        
        // Customize price axis
        plot.getRangeAxis().setLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        plot.getRangeAxis().setTickLabelPaint(ColorScheme.LIGHT_GRAY_COLOR);
        
        // Customize series colors and tooltips
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, ColorScheme.PROGRESS_ERROR_COLOR);
        renderer.setSeriesPaint(1, ColorScheme.PROGRESS_COMPLETE_COLOR);
        renderer.setSeriesShape(0, new Rectangle(-3, -3, 6, 6));
        renderer.setSeriesShape(1, new Rectangle(-3, -3, 6, 6));
        renderer.setSeriesToolTipGenerator(0, new PriceToolTipGenerator("Buy"));
        renderer.setSeriesToolTipGenerator(1, new PriceToolTipGenerator("Sell"));
        plot.setRenderer(renderer);
        
        // Create chart panel with enhanced interactivity
        chartPanel = new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 200);
            }
        };
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setZoomInFactor(0.5);
        chartPanel.setZoomOutFactor(2);
        chartPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        
        add(chartPanel, BorderLayout.CENTER);
    }
    
    // Custom tooltip generator
    private class PriceToolTipGenerator implements XYToolTipGenerator {
        private final String type;
        
        public PriceToolTipGenerator(String type) {
            this.type = type;
        }
        
        @Override
        public String generateToolTip(XYDataset dataset, int series, int item) {
            double price = dataset.getYValue(series, item);
            long time = (long) dataset.getXValue(series, item);
            Date date = new Date(time);
            
            return String.format("<html><b>%s Price:</b> %s gp<br><b>Time:</b> %s</html>",
                type,
                GP_FORMAT.format((int)price),
                TIME_FORMAT.format(date)
            );
        }
    }
    
    public void addPrice(int buyPrice, int sellPrice) {
        Minute currentMinute = new Minute(new Date());
        buyPriceSeries.addOrUpdate(currentMinute, buyPrice);
        sellPriceSeries.addOrUpdate(currentMinute, sellPrice);
        
        // Remove old data points (keep last 24 hours)
        while (buyPriceSeries.getItemCount() > 1440) { // 24 hours * 60 minutes
            RegularTimePeriod firstBuyPeriod = buyPriceSeries.getTimePeriod(0);
            RegularTimePeriod firstSellPeriod = sellPriceSeries.getTimePeriod(0);
            buyPriceSeries.delete(firstBuyPeriod);
            sellPriceSeries.delete(firstSellPeriod);
        }
    }
    
    public void clearData() {
        buyPriceSeries.clear();
        sellPriceSeries.clear();
    }
}