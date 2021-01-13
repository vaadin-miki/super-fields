export class DatePatternMixin {
    static to(superclass) { return class extends superclass {

        initPatternSetting(datepicker) {
            console.log("SDP: init")

            if (datepicker.oldSetLocale === undefined) {
                console.log("SDP: old set locale method not there, holding reference");
                if (datepicker.$connector === undefined) {
                    console.error("SDP: no connector set, things will not work until it is set");
                    return;
                }
                datepicker.set('oldSetLocale', datepicker.$connector.setLocale);
                datepicker.$connector.setLocale = locale => {
                        console.log('SDP: setting locale ' + locale);
                        datepicker.oldSetLocale(locale);
                        datepicker.noPatternParseAndFormat = [datepicker.i18n.parseDate, datepicker.i18n.formatDate];

                        if (datepicker.i18n.dateDisplayPattern && datepicker.i18n.dateDisplayPattern.length > 0) {
                            console.log("SDP: date pattern existing, setting it again");
                            datepicker.noPatternParseAndFormat = [datepicker.i18n.parseDate, datepicker.i18n.formatDate];
                            this.setDisplayPattern(datepicker, datepicker.i18n.dateDisplayPattern);
                        }
                    }

            }
        }

        setDisplayPattern(datepicker, displayPattern) {
            if (datepicker.$connector === undefined) {
                console.error("SDP: Connector not available. Cannot continue.");
                return
            }
            console.log('SDP: MAIN - set display pattern');
            // the pattern parts:
            // - separator (1) - optional
            // - day, month, year (3 x 2 = 6) - required
            // - century indicator, default century and boundary year (1 + 2 + 2 = 5) - optional
            // this gives: 7 or 12 characters when the separator is present, 6 or 11 when it is not, and 0/null for none

            // pattern is a string, first optional character is a separator
            // next six characters define, in groups of 2, the order and what should be displayed
            // 0d or _d - (zero prefixed) day
            // 0M or _M - (zero prefixed) month
            // 0y or _y - short year or full year
            // the remaining of the pattern is optional, only when 0y is used
            // + or - - corresponds to previous century in short year (+ when true, - when false)
            // XX - number corresponding to the default century in short years: MUST BE TWO DIGITS
            // YY - number corresponding to the boundary year: MUST BE TWO DIGITS
            if (displayPattern === null) {
                console.log("SDP: clearing date display pattern");
                datepicker.i18n.dateDisplayPattern = '';
                if (datepicker.noPatternParseAndFormat) {
                    datepicker.set('i18n.parseDate', datepicker.noPatternParseAndFormat[0]);
                    datepicker.set('i18n.formatDate', datepicker.noPatternParseAndFormat[1]);
                    delete datepicker.noPatternParseAndFormat;
                }
            } else {
                console.log('SDP: setting date display pattern to <' + displayPattern + '>');
                datepicker.i18n.dateDisplayPattern = displayPattern;
                if (!datepicker.noPatternParseAndFormat) {
                    datepicker.noPatternParseAndFormat = [datepicker.i18n.parseDate, datepicker.i18n.formatDate];
                }
                datepicker.set('i18n.formatDate', date => {
                    const ddp = datepicker.i18n.dateDisplayPattern;
                    const monthNames = datepicker.i18n.displayMonthNames;
                    const startIndex = (ddp.length === 7 || ddp.length === 12) ? 1 : 0;
                    console.log('SDP: custom formatting for ' + ddp);
                    return [ddp.substr(startIndex, 2), ddp.substr(startIndex+2, 2), ddp.substr(startIndex+4, 2)].map(part => {
                        if (part === '0d') {
                            return String(date.day).padStart(2, '0')
                        } else if (part === '_d') {
                            return String(date.day)
                        } else if (part === '0M') {
                            return String(date.month + 1).padStart(2, '0')
                        } else if (part === '_M') {
                            return String(date.month + 1)
                        } else if (part === 'mM') {
                            return monthNames[date.month]
                        } else if (part === '0y') {
                            return date.year < 10 ? '0' + String(date.year) : String(date.year).substr(-2)
                        } else if (part === '_y') {
                            return String(date.year)
                        }
                    }).join(startIndex === 1 ? ddp[0] : '');
                });
                datepicker.set('i18n.parseDate', text => {
                    const ddp = datepicker.i18n.dateDisplayPattern;
                    const shortYear = ddp.indexOf('0y') !== -1;
                    const useMonthName = ddp.indexOf('mM') >= 0;
                    const monthNames = datepicker.i18n.displayMonthNames;
                    console.log('SDP: custom parsing for ' + ddp);
                    const today = new Date();
                    let date, month = today.getMonth(), year = today.getFullYear();
                    // this part is triggered when there is a separator
                    if (ddp.length === 7 || ddp.length === 12) {
                        const parts = text.split(ddp[0]);
                        if (parts.length === 3) {
                            // d, M, y can be at index 2, 4 or 6 in the pattern
                            year = parseInt(parts[(ddp.indexOf('y') / 2) - 1]);
                            if (useMonthName) {
                                const userInput = parts[(ddp.indexOf('M') / 2) - 1];
                                month = monthNames.indexOf(monthNames.find(m => m.substring(0, userInput.length).localeCompare(userInput, undefined, {sensitivity: 'base'}) === 0));
                                // this is here in case the locale was changed or the month was not found
                                if (month === -1) {
                                    month = parseInt(datepicker.value.substring(5, 7)) - 1;
                                }
                            } else {
                                month = parseInt(parts[(ddp.indexOf('M') / 2) - 1]) - 1;
                            }
                            date = parseInt(parts[(ddp.indexOf('d') / 2) - 1]);
                        } else if (parts.length === 2) {
                            if (ddp.indexOf('d') < ddp.indexOf('M')) {
                                date = parseInt(parts[0]);
                                if (useMonthName) {
                                    month = monthNames.indexOf(monthNames.find(m => m.substring(0, parts[1].length).localeCompare(parts[1], undefined, {sensitivity: 'base'}) === 0));
                                    // this is here in case the locale was changed or the month was not found
                                    if (month === -1) {
                                        month = parseInt(datepicker.value.substring(5, 7)) - 1;
                                    }
                                } else {
                                    month = parseInt(parts[1]) - 1;
                                }
                            } else {
                                date = parseInt(parts[1]);
                                if (useMonthName) {
                                    month = monthNames.indexOf(monthNames.find(m => m.substring(0, parts[0].length).localeCompare(parts[0], undefined, {sensitivity: 'base'}) === 0));
                                    // this is here in case the locale was changed or the month was not found
                                    if (month === -1) {
                                        month = parseInt(datepicker.value.substring(5, 7)) - 1;
                                    }
                                } else {
                                    month = parseInt(parts[0]) - 1;
                                }
                            }
                        } else if (parts.length === 1) {
                            date = parseInt(parts[0]);
                        }
                    }
                    // there is no separator and thus by definition month and day are zero-based
                    // this means the pattern starts directly with day/month/year parts, each taking two characters
                    // it also means that the input is composed of parts with two eventually up to four characters
                    else {
                        const dayOrder = Math.floor(ddp.indexOf('d') / 2);
                        const monthOrder = Math.floor(ddp.indexOf('M') / 2);
                        const yearOrder = Math.floor(ddp.indexOf('y') / 2);
                       // if the length of the input is 1 or 2, it is a day
                       if (text.length <= 2) {
                           date = parseInt(text);
                       }
                       // length being 3 or 4 means there is a day and a month, in order defined by the pattern
                       else if (text.length <= 4) {
                           // day first
                           if (dayOrder < monthOrder) {
                               date = parseInt(text.substr(0, 2));
                               month = parseInt(text.substr(2)) - 1;
                           }
                           // month first
                           else {
                               month = parseInt(text.substr(0, 2));
                               date = parseInt(text.substr(2)) - 1;
                           }
                       }
                       // length is more than 4 characters, which means there is also year involved
                       else {
                           let yearPosition = yearOrder * 2;
                           let dayPosition = dayOrder * 2;
                           let monthPosition = monthOrder * 2;
                           // if year is full, month and day can potentially be offset by 2 extra characters
                           if (!shortYear) {
                               if (dayOrder > yearOrder)
                                   dayPosition += 2;
                               if (monthOrder > yearOrder)
                                   monthPosition += 2;
                           }
                           date = parseInt(text.substr(dayPosition, 2));
                           month = parseInt(text.substr(monthPosition, 2)) - 1;
                           year = parseInt(text.substr(yearPosition, shortYear ? 2 : 4));
                       }
                    }
                    // end of parsing stuff
                    // now, if short year is allowed (i.e. there is a description of default century and boundary year)
                    if (ddp.length === 12 || ddp.length === 11) {
                        const boundaryYear = parseInt(ddp.substr(-2));
                        const defaultCentury = parseInt(ddp.substr(-4, 2));
                        if (year < boundaryYear) {
                            year += (ddp[ddp.length-5] === '+' ? defaultCentury - 2 : defaultCentury - 1) * 100;
                        } else if (year < 100) {
                            year += (ddp[ddp.length-5] === '+' ? defaultCentury - 1 : defaultCentury - 2) * 100;
                        }
                        console.log("SDP: after fixing the year is "+year);
                    }
                    // return result
                    if (date !== undefined) {
                        return {day: date, month, year};
                    }
                });
            }
        }
    }
    }
}