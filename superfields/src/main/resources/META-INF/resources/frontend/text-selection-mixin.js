export class TextSelectionMixin {
    static to(superclass) {
        return class extends superclass {

            updateData(data, src) {
                const currentStart = data.startsAt;
                const currentEnd = data.endsAt;
                // there is some selection
                if (data.input.selectionStart !== undefined && data.input.selectionStart !== data.input.selectionEnd) {
                    data.startsAt = data.input.selectionStart;
                    data.endsAt = data.input.selectionEnd;
                    data.selection = data.input.value.substring(data.startsAt, data.endsAt);
                }
                else {
                    data.startsAt = -1;
                    data.endsAt = -1;
                    data.selection = '';
                }
                src.dataset.selectedText = data.selection;
                if(data.callServer && (currentStart !== data.startsAt || currentEnd !== data.endsAt)) {
                    console.log('TSM: calling server');
                    src.$server.selectionChanged(data.startsAt, data.endsAt, data.selection);
                }
            }

            selectAll(src) {
                console.log('TSM: selecting all text');
                src.selectionMixin.input.select();
                src.updateData(src.selectionMixin, src);
            }

            selectNone(src) {
                console.log('TSM: selecting no text');
                src.selectionMixin.input.selectionStart = src.selectionMixin.input.selectionEnd;
                src.updateData(src.selectionMixin, src)
            }

            select(src, from, to) {
                console.log('TSM: selecting from '+from+' to '+to);
                if (from <= to) {
                    src.selectionMixin.input.selectionStart = from;
                    src.selectionMixin.input.selectionEnd = to;
                    src.updateData(src.selectionMixin, src);
                }
            }

            replaceText(src, text, from, to) {
                console.log('TSM: replacing text '+text+' from '+from+' to '+to);
                if (from < 0) {
                    from = src.selectionMixin.input.selectionStart;
                }
                if (to < 0) {
                    to = src.selectionMixin.input.selectionEnd;
                }
                src.selectionMixin.input.setRangeText(text, from, to);
                // the above code does not trigger value changes
                // so using the trick from clear-button handler
                const inputEvent = new Event('input', { bubbles: true, composed: true });
                const changeEvent = new Event('change', { bubbles: !src._slottedInput });
                src.selectionMixin.input.dispatchEvent(inputEvent);
                src.selectionMixin.input.dispatchEvent(changeEvent);
            }

            listenToEvents(inputComponent, webComponent, notifyServer) {
                console.log('TSM: setting up text selection for component <'+webComponent.tagName+'>');
                if (inputComponent === undefined) {
                    console.log('TSM: input component is undefined, attempting to find it from shadow root/input element');
                    inputComponent = webComponent.inputElement;
                    if (inputComponent === undefined) {
                        console.log('TSM: no input component, server will reinitialise this component shortly (probably used inside Grid, nothing to worry about)');
                        // this trick has been suggested by the magnificent Erik Lumme, thank you!
                        webComponent.$server.reinitialiseListening();
                    }
                }
                if (inputComponent !== undefined) {
                    console.log('TSM: input component is ' + inputComponent);
                    if (webComponent.selectionMixin === undefined) {
                        webComponent.selectionMixin = {
                            input: inputComponent,
                            callServer: notifyServer,
                            startsAt: -1,
                            endsAt: -1,
                            selection: ''
                        }

                        const listener = () => webComponent.updateData(webComponent.selectionMixin, webComponent);
                        inputComponent.addEventListener('mouseup', listener);
                        inputComponent.addEventListener('keyup', listener);
                        inputComponent.addEventListener('mouseleave', listener);
                    } else {
                        webComponent.selectionMixin.callServer = notifyServer;
                    }
                }
            }
        }
    }
}