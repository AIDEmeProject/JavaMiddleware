import React, { Component } from 'react';

import axios from "axios" 
import $ from 'jquery'

import {webplatformApi} from '../constants/constants'

import logo from '../AIDEME.png'
import MicroModal from 'micromodal'

class MicroModalComponent extends Component{

  render(){

    return (
            
        <div class="modal micromodal-slide" id="modal-1" aria-hidden="true">
            <div class="modal__overlay" tabIndex="-1" data-micromodal-close>
            <div class="modal__container" role="dialog" aria-modal="true" aria-labelledby="modal-1-title">
                <header class="modal__header">
                <h2 class="modal__title" id="modal-1-title">
                    {this.props.title}
                </h2>
                <button class="modal__close" aria-label="Close modal" data-micromodal-close></button>
                </header>
                <main class="modal__content" id="modal-1-content">
                <div>
                    {this.props.children}
                </div>
                </main>
                <footer class="modal__footer">
                <button class="modal__btn modal__btn-primary">Continue</button>
                <button class="modal__btn" data-micromodal-close aria-label="Close this dialog window">Close</button>
                </footer>
            </div>
            </div>
        </div>
    )
    
  }

  componentDidMount(){
      MicroModal.init()
      MicroModal.show('modal-1')
  }
}


MicroModalComponent.defaultProps = {
    title: "title"
}

export default MicroModalComponent