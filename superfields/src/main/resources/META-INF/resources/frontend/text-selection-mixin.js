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

            listenToEvents(inputComponent, webComponent, notifyServer) {
                console.log('TSM: setting up text selection for component <'+webComponent.tagName+'>');
                if (inputComponent === undefined) {
                    console.log('TSM: input component is undefined, attempting to find it from shadow root/input element');
                    inputComponent = webComponent.inputElement;
                    if (inputComponent === undefined) {
                        console.warn('TSM: no input component, server will reinitialise this component shortly');
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