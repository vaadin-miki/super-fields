export class TextGrowMixin {
    static to(superclass) {
        return class extends superclass {

            growsWithInput(event) {
                let src = event.target;
                if (src.dataset.growsWithInput) {
                    src.style.width = src.focusElement.scrollWidth + "px";
                }
            }

            configureGrowingWithInput(src) {
                src.addEventListener('input', this.growsWithInput)
            }

            setGrowWithInput(src, state) {
                if (state) {
                    src.dataset.growsWithInput = 'true';
                }
                else {
                    delete src.dataset.growsWithInput;
                    delete src.style.width;
                }
            }

        }
    }
}
