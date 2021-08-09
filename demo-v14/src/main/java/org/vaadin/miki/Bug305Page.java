package org.vaadin.miki;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.miki.shared.dates.DatePattern;
import org.vaadin.miki.superfields.dates.SuperDatePicker;

@Route("bug-305")
@PageTitle("Bug #305")
public class Bug305Page extends VerticalLayout {

    public Bug305Page() {
        SuperDatePicker startDatePicker = new SuperDatePicker();
        startDatePicker.setId("client-admin-tabs-decisions_search-form_date-start");

        DatePattern datePattern = new DatePattern();
        datePattern.setDisplayOrder(DatePattern.Order.YEAR_MONTH_DAY);
        datePattern.setSeparator('-');

        startDatePicker.setDatePattern(datePattern);
        startDatePicker.setPlaceholder("YYYY-MM-DD");
        startDatePicker.setErrorMessage("Invalid date format");

        SuperDatePicker endDatePicker = new SuperDatePicker();
        endDatePicker.setId("client-admin-tabs-decisions_search-form_date-end");
        endDatePicker.setDatePattern(datePattern);
        endDatePicker.setPlaceholder("YYYY-MM-DD");
        endDatePicker.setErrorMessage("Invalid date format");

        HorizontalLayout dateRangeLayout = new HorizontalLayout();
        dateRangeLayout.setSpacing(false);
        dateRangeLayout.add(new Text("Date issued between"));

        startDatePicker.setWidth("350px");
        dateRangeLayout.add(startDatePicker);

        Text andLabel = new Text("search-panel-date-and");
        HorizontalLayout andLayout = new HorizontalLayout(andLabel);
        andLayout.setWidth("100px");

        dateRangeLayout.add(andLayout);

        endDatePicker.setWidth("350px");
        dateRangeLayout.add(endDatePicker);
        this.add(dateRangeLayout);
    }

}
