import React, { useState, useEffect } from 'react';
import './TransactionModal.css';

function BillModal({ bill, onClose, onSave, onDelete }) {
    const [formData, setFormData] = useState({
        name: '',
        transactionDescription: '',
        amount: '',
        scheduleType: '',
        scheduleValue: '',
        start: ''
    });

    useEffect(() => {
        if (bill) {
            setFormData({
                name: bill.name || '',
                transactionDescription: bill.transactionDescription || '',
                amount: bill.amount || '',
                scheduleType: bill.scheduleType || '',
                scheduleValue: bill.scheduleValue || '',
                start: bill.start || ''
            });
        }
    }, [bill]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSave(bill.id, formData);
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this bill? This action cannot be undone.')) {
            await onDelete(bill.id);
        }
    };

    if (!bill) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Edit bill</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label>Bill ID</label>
                        <input type="text" value={bill.id} disabled />
                    </div>

                    <div className="form-group">
                        <label>Bill Name *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.transactionDescription}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description *</label>
                        <input
                            type="text"
                            name="scheduleType"
                            value={formData.scheduleType}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description *</label>
                        <input
                            type="text"
                            name="scheduleValue"
                            value={formData.scheduleValue}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Amount *</label>
                            <input
                                type="number"
                                step="0.01"
                                name="limitAmount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="modal-actions">
                        <button
                            type="button"
                            onClick={handleDelete}
                            className="btn-danger"
                        >
                            Delete
                        </button>
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Save Changes
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default BillModal;