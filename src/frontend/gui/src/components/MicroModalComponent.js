import React, { Component } from 'react';

import axios from "axios" 
import $ from 'jquery'

import {webplatformApi} from '../constants/constants'

import logo from '../AIDEME.png'
import MicroModal from 'micromodal'

class MicroModalComponent extends Component{

  render(){

    return (
            
        <div 
            className="modal micromodal-slide" 
            id="modal-1" aria-hidden="true"
        >
            <div className="modal__overlay" tabIndex="-1" data-micromodal-close>
            <div className="modal__container" role="dialog" aria-modal="true" aria-labelledby="modal-1-title">
                <header className="modal__header">
                <h2 className="modal__title" id="modal-1-title">
                    {this.props.title}
                </h2>
                <button
                     className="modal__close"                     
                     
                    onClick={this.props.onClose}
                    role="button"
                    type="button"
                >

                </button>
                </header>
                <main className="modal__content" id="modal-1-content">
                <div>
                    {this.props.children}
                </div>
                </main>
                <footer className="modal__footer">
                
                <button
                    className="modal__btn" 
                    onClick={this.props.onClose}
                    role="button"
                    type="button"
                >
                    Close</button>
                </footer>
            </div>
            </div>
        </div>
    )
    
  }

  componentDidMount(){
      MicroModal.init({
        onClose: () => {MicroModal.close('modal-1'); this.props.onClose()}
      })
      MicroModal.show('modal-1')
  }

  componentWillUnmount(){
      MicroModal.close('modal-1')
  }
}


MicroModalComponent.defaultProps = {
    title: "title"
}

export default MicroModalComponent