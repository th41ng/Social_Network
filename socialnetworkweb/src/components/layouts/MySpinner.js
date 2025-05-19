import React from 'react';
import { Spinner as BootstrapSpinner } from 'react-bootstrap';

const MySpinner = () => {
    return (
        <div className="text-center my-3"> 
            <BootstrapSpinner animation="border" role="status">
                <span className="visually-hidden">Loading...</span>
            </BootstrapSpinner>
        </div>
    );
};

export default MySpinner; 