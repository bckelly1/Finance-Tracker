import React, { useState, useEffect } from 'react';
import './TransactionModal.css';

function BillModal({ bill, onClose, onSave, onDelete }) {
    const [formData, setFormData] = useState({
        name: '',
        amount: '',
        period: 'MONTHLY',
        start: ''
    });

    useEffect(() => {
        if (bill) {
            setFormData({
                name: bill.name || '',
                amount: bill.limitAmount || '',
                period: bill.period || 'MONTHLY',
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

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount);
    };

    if (!bill) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Edit bill</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <div className="bill">
                    <div className="summary-item">
                        <span className="summary-label">Spent</span>
                        <span className="summary-value">{formatCurrency(bill.amount)}</span>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label>bill ID</label>
                        <input type="text" value={bill.id} disabled />
                    </div>

                    <div className="form-group">
                        <label>bill Name *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Limit Amount *</label>
                            <input
                                type="number"
                                step="0.01"
                                name="limitAmount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Period *</label>
                            <select
                                name="period"
                                value={formData.period}
                                onChange={handleChange}
                                required
                            >
                                <option value="WEEKLY">Weekly</option>
                                <option value="MONTHLY">Monthly</option>
                                <option value="YEARLY">Yearly</option>
                            </select>
                        </div>
                    </div>


                    <div className="form-group">
                        <label>Start Date</label>
                        <input
                            type="date"
                            name="start"
                            value={formData.start}
                            onChange={handleChange}
                        />
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