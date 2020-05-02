// this is a copy of datepickerConnector.js, but with few changes to support date display pattern
// I have no idea what I am doing, but this seems to be the only way to get it to work
// except where noted, this code has been written by Vaadin
(function () {
    const tryCatchWrapper = function (callback) {
        return window.Vaadin.Flow.tryCatchWrapper(callback, 'Vaadin Date Picker', 'vaadin-date-picker-flow');
    };

    /* helper class for parsing regex from formatted date string */

    class FlowDatePickerPart {
        constructor(initial) {
            this.initial = initial;
            this.index = 0;
            this.value = 0;
        }

        static compare(part1, part2) {
            if (part1.index < part2.index) {
                return -1;
            }
            if (part1.index > part2.index) {
                return 1;
            }
            return 0;
        }
    }
    window.Vaadin.Flow.datepickerConnector = {
        initLazy: datepicker => tryCatchWrapper(function (datepicker) {
            // Check whether the connector was already initialized for the datepicker
            if (datepicker.$connector) {
                return;
            }

            datepicker.$connector = {};

            /* init helper parts for reverse-engineering date-regex */
            datepicker.$connector.dayPart = new FlowDatePickerPart("22");
            datepicker.$connector.monthPart = new FlowDatePickerPart("11");
            datepicker.$connector.yearPart = new FlowDatePickerPart("1987");
            datepicker.$connector.parts = [datepicker.$connector.dayPart, datepicker.$connector.monthPart, datepicker.$connector.yearPart];

            // Old locale should always be the default vaadin-date-picker component
            // locale {English/US} as we init lazily and the date-picker formats
            // the date using the default i18n settings and we need to use the input
            // value as we may need to parse user input so we can't use the _selectedDate value.
            let oldLocale = "en-us";

            datepicker.addEventListener('blur', tryCatchWrapper(e => {
                if (!e.target.value && e.target.invalid) {
                    console.warn("Invalid value in the DatePicker.");
                }
            }));

            const cleanString = tryCatchWrapper(function (string) {
                // Clear any non ascii characters from the date string,
                // mainly the LEFT-TO-RIGHT MARK.
                // This is a problem for many Microsoft browsers where `toLocaleDateString`
                // adds the LEFT-TO-RIGHT MARK see https://en.wikipedia.org/wiki/Left-to-right_mark
                return string.replace(/[^\x00-\x7F]/g, "");
            });

            const getInputValue = tryCatchWrapper(function () {
                let inputValue = '';
                try {
                    inputValue = datepicker._inputValue;
                } catch(err) {
                    /* component not ready: falling back to stored value */
                    inputValue = datepicker.value || '';
                }
                return inputValue;
            });

            // this method has been written by Miki
            datepicker.$connector.setDisplayPattern = tryCatchWrapper(function (displayPattern) {
                // pattern is a string, first character is a separator
                // next six characters define, in groups of 2, the order and what should be displayed
                // 0d or _d - (zero prefixed) day
                // 0M or _M - (zero prefixed) month
                // 0y or _y - short year or full year
                // the remaining of the pattern is optional, only when yy is used
                // + or - - corresponds to previous century in short year (+ when true, - when false)
                // XX - number corresponding to the default century in short years: MUST BE TWO DIGITS
                // YY - number corresponding to the boundary year: MUST BE TWO DIGITS
                if (displayPattern === null) {
                    console.log("clearing date display pattern");
                    datepicker.i18n.dateDisplayPattern = '';
                }
                else {
                    console.log('setting date display pattern to <'+displayPattern+'>');
                    datepicker.i18n.dateDisplayPattern = displayPattern;
                    datepicker.set('i18n.formatDate', tryCatchWrapper(function (date) {
                        const ddp = datepicker.i18n.dateDisplayPattern;
                        return [ddp.substr(1, 2), ddp.substr(3, 2), ddp.substr(5, 2)].map(part => {
                            if (part === '0d') {
                                return String(date.day).padStart(2, '0')
                            } else if (part === '_d') {
                                return String(date.day)
                            } else if (part === '0M') {
                                return String(date.month + 1).padStart(2, '0')
                            } else if (part === '_M') {
                                return String(date.month + 1)
                            } else if (part === '0y') {
                                return date.year < 10 ? '0' + String(date.year) : String(date.year).substr(-2)
                            } else if (part === '_y') {
                                return String(date.year)
                            }
                        }).join(ddp[0]);
                    }));
                    datepicker.set('i18n.parseDate', tryCatchWrapper(function (text) {
                        const ddp = datepicker.i18n.dateDisplayPattern;
                        const today = new Date();
                        let date, month = today.getMonth(), year = today.getFullYear();
                        const parts = text.split(ddp[0]);
                        if (parts.length === 3) {
                            // d, M, y can be at index 2, 4 or 6 in the pattern
                            year = parseInt(parts[(ddp.indexOf('y') / 2) - 1]);
                            month = parseInt(parts[(ddp.indexOf('M') / 2) - 1]) - 1;
                            date = parseInt(parts[(ddp.indexOf('d') / 2) - 1]);
                            // now, if short year is used
                            if (ddp.indexOf('0y') !== -1) {
                                const boundaryYear = parseInt(ddp.substr(-2));
                                const defaultCentury = parseInt(ddp.substr(-4, 2));
                                if (year < boundaryYear) {
                                    year += (ddp[7] === '+' ? defaultCentury - 2 : defaultCentury - 1) * 100;
                                } else if (year < 100) {
                                    year += (ddp[7] === '+' ? defaultCentury - 2 : defaultCentury - 1) * 100;
                                }
                            }
                        } else if (parts.length === 2) {
                            if (ddp.indexOf('d') < ddp.indexOf('M')) {
                                date = parseInt(parts[0]);
                                month = parseInt(parts[1]) - 1;
                            } else {
                                date = parseInt(parts[1]);
                                month = parseInt(parts[0]) - 1;
                            }
                        } else if (parts.length === 1) {
                            date = parseInt(parts[0]);
                        }
                        if (date !== undefined) {
                            return {day: date, month, year};
                        }
                    }));
                }
            });
            // end of code written by Miki

            datepicker.$connector.setLocale = tryCatchWrapper(function (locale) {

                try {
                    // Check whether the locale is supported or not
                    new Date().toLocaleDateString(locale);
                } catch (e) {
                    locale = "en-US";
                    console.warn("The locale is not supported, using default locale setting(en-US).");
                }

                let currentDate = false;
                let inputValue = getInputValue();
                if (datepicker.i18n.parseDate !== 'undefined' && inputValue) {
                    /* get current date with old parsing */
                    currentDate = datepicker.i18n.parseDate(inputValue);
                }

                /* create test-string where to extract parsing regex */
                let testDate = new Date(Date.UTC(datepicker.$connector.yearPart.initial, datepicker.$connector.monthPart.initial - 1, datepicker.$connector.dayPart.initial));
                let testString = cleanString(testDate.toLocaleDateString(locale, { timeZone: 'UTC' }));
                datepicker.$connector.parts.forEach(function (part) {
                    part.index = testString.indexOf(part.initial);
                });
                /* sort items to match correct places in regex groups */
                datepicker.$connector.parts.sort(FlowDatePickerPart.compare);
                /* create regex
                * regex will be the date, so that:
                * - day-part is '(\d{1,2})' (1 or 2 digits),
                * - month-part is '(\d{1,2})' (1 or 2 digits),
                * - year-part is '(\d{1,4})' (1 to 4 digits)
                *
                * and everything else is left as is.
                * For example, us date "10/20/2010" => "(\d{1,2})/(\d{1,2})/(\d{1,4})".
                *
                * The sorting part solves that which part is which (for example,
                * here the first part is month, second day and third year)
                *  */
                datepicker.$connector.regex = testString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&')
                    .replace(datepicker.$connector.dayPart.initial, "(\\d{1,2})")
                    .replace(datepicker.$connector.monthPart.initial, "(\\d{1,2})")
                    .replace(datepicker.$connector.yearPart.initial, "(\\d{1,4})");

                // this next line (if condition) added by Miki
                if (datepicker.i18n.dateDisplayPattern === undefined || datepicker.i18n.dateDisplayPattern.length === 0) {
                    datepicker.i18n.formatDate = tryCatchWrapper(function (date) {
                        let rawDate = datepicker._parseDate(`${date.year}-${date.month + 1}-${date.day}`);

                        // Workaround for Safari DST offset issue when using Date.toLocaleDateString().
                        // This is needed to keep the correct date in formatted result even if Safari
                        // makes an error of an hour or more in the result with some past dates.
                        // See https://github.com/vaadin/vaadin-date-picker-flow/issues/126#issuecomment-508169514
                        rawDate.setHours(12)

                        return cleanString(rawDate.toLocaleDateString(locale));
                    });

                    datepicker.i18n.parseDate = tryCatchWrapper(function (dateString) {
                        dateString = cleanString(dateString);

                        if (dateString.length == 0) {
                            return;
                        }

                        let match = dateString.match(datepicker.$connector.regex);
                        if (match && match.length == 4) {
                            for (let i = 1; i < 4; i++) {
                                datepicker.$connector.parts[i - 1].value = parseInt(match[i]);
                            }
                            return {
                                day: datepicker.$connector.dayPart.value,
                                month: datepicker.$connector.monthPart.value - 1,
                                year: datepicker.$connector.yearPart.value
                            };
                        } else {
                            return false;
                        }
                    });
                }
                if (inputValue === "") {
                    oldLocale = locale;
                } else if (currentDate) {
                    /* set current date to invoke use of new locale */
                    datepicker._selectedDate = datepicker._parseDate(`${currentDate.year}-${currentDate.month + 1}-${currentDate.day}`);
                }
            });
        })(datepicker)
    };
})();