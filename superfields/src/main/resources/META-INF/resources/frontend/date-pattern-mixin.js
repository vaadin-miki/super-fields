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

        setDisplayPattern(datepicker, displayPattern, hasServer) {
            if (datepicker.$connector === undefined) {
                console.error("SDP: Connector not available. Cannot continue.");
                return
            }
            if (hasServer === undefined) {
                hasServer = datepicker;
            }
            console.log('SDP: MAIN - set display pattern');
            if (displayPattern === null) {
                console.log("SDP: clearing date display pattern");
                let wasServer = (datepicker.i18n.dateDisplayPattern === '$server');
                datepicker.i18n.dateDisplayPattern = '';
                if (datepicker.noPatternParseAndFormat) {
                    datepicker.set('i18n.parseDate', datepicker.noPatternParseAndFormat[0]);
                    datepicker.set('i18n.formatDate', datepicker.noPatternParseAndFormat[1]);
                    // explicitly update the pattern when server callback was used
                    // no idea why it is needed, but probably because of some async stuff?
                    if (wasServer) {
                        datepicker.shadowRoot.querySelector('#input').value = datepicker.noPatternParseAndFormat[1](datepicker._parseDate(datepicker.value));
                    }
                    delete datepicker.noPatternParseAndFormat;
                }
            }
            // needs server callback
            // there is some magic here, because server callbacks are asynchronous (return promises)
            // and of course the rest of the code is totally synchronous
            // this solution is probably far from ideal, but it seems to work :)
            else if (displayPattern === '$server') {
                let parseOnServer;
                let formatOnServer;
                formatOnServer = function(date) {
                    datepicker.set('i18n.parseDate', d => date);
                    console.log('SDP: querying server for formatting date');
                    hasServer.$server.formatDate(date.year, date.month+1, date.day).then(formatted => {
                        console.log('SDP: value formatted as '+formatted);
                        datepicker.shadowRoot.querySelector('#input').value = formatted;
                        datepicker.value = date;
                        datepicker.set('i18n.parseDate', parseOnServer);
                    });
                    return datepicker._formatISO(date);
                }

                parseOnServer = function(text) {
                    console.log('SDP: querying server for parsing date from <'+text+'>');
                    if (text !== undefined && text !== null && text.length > 0) {
                        datepicker.set('i18n.formatDate', t => text);
                        hasServer.$server.parseDate(text).then(parsed => {
                            datepicker.value = parsed;
                            datepicker.set('i18n.formatDate', formatOnServer);
                        });
                    }
                    return null;
                }

                datepicker.set('i18n.formatDate', formatOnServer);
                datepicker.set('i18n.parseDate', parseOnServer);
            }
            // no callback needed:
            // pattern is a string, first character is a separator
            // next six characters define, in groups of 2, the order and what should be displayed
            // 0d or _d - (zero prefixed) day
            // 0M or _M - (zero prefixed) month
            // 0y or _y - short year or full year
            // the remaining of the pattern is optional, only when yy is used
            // + or - - corresponds to previous century in short year (+ when true, - when false)
            // XX - number corresponding to the default century in short years: MUST BE TWO DIGITS
            // YY - number corresponding to the boundary year: MUST BE TWO DIGITS
            else {
                console.log('SDP: setting date display pattern to <' + displayPattern + '>');
                datepicker.i18n.dateDisplayPattern = displayPattern;
                if (!datepicker.noPatternParseAndFormat) {
                    datepicker.noPatternParseAndFormat = [datepicker.i18n.parseDate, datepicker.i18n.formatDate];
                }
                datepicker.set('i18n.formatDate', date => {
                    // debugger;
                    const ddp = datepicker.i18n.dateDisplayPattern;
                    console.log('SDP: custom formatting for ' + ddp + ' and date <'+ JSON.stringify(date) + '>');
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
                });
                datepicker.set('i18n.parseDate', text => {
                    // debugger;
                    const ddp = datepicker.i18n.dateDisplayPattern;
                    console.log('SDP: custom parsing for ' + ddp + ' and text <' + text + '>');
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
                });
            }
        }
    }
    }
}