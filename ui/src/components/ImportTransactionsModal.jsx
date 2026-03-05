import React, { useState } from 'react';
import './TransactionModal.css';

function ImportTransactionsModal({ onClose, onCreate }) {
    const [selectedFile, setSelectedFile] = useState(null);

    const handleChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onCreate(selectedFile);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Import New Transactions</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="modal-content">
                        <p>
                            Download the{' '}
                            <a href="/csv-template.csv" download>
                                CSV template
                            </a>{' '}
                            to see the expected import format.
                        </p>
                    </div>
                    <div className="form-group">
                        <label>File</label>
                        <input
                            type="file"
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="modal-actions">
                        <div></div> {/* Empty div for spacing */}
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Import Transactions
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ImportTransactionsModal;