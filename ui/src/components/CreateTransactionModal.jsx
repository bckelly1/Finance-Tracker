import React, { useState } from 'react';
import './TransactionModal.css';

function CreateTransactionModal({ categories, onClose, onCreate }) {
    const [formData, setFormData] = useState({
        date: new Date().toISOString().split('T')[0] + 'T' + new Date().toTimeString().split(' ')[0],
        description: '',
        originalDescription: '',
        amount: '',
        type: 'Debit',
        category: '',
        vendor: '',
        accountName: '',
        mailMessageId: '',
        notes: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onCreate(formData);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Create New Transaction</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label>Date & Time *</label>
                        <input
                            type="datetime-local"
                            name="date"
                            value={formData.date}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Description *</label>
                        <input
                            type="text"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            required
                            placeholder="Enter transaction description"
                        />
                    </div>

                    <div className="form-group">
                        <label>Original Description</label>
                        <input
                            type="text"
                            name="originalDescription"
                            value={formData.originalDescription}
                            onChange={handleChange}
                            placeholder="Original description from source"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Amount *</label>
                            <input
                                type="number"
                                step="0.01"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                                placeholder="0.00"
                            />
                        </div>

                        <div className="form-group">
                            <label>Type *</label>
                            <select
                                name="type"
                                value={formData.type}
                                onChange={handleChange}
                                required
                            >
                                <option value="Debit">Debit</option>
                                <option value="Credit">Credit</option>
                            </select>
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Category</label>
                            <select
                                name="category"
                                value={formData.category}
                                onChange={handleChange}
                            >
                                <option value="">Select Category</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.name}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Vendor</label>
                            <input
                                type="text"
                                name="vendor"
                                value={formData.vendor}
                                onChange={handleChange}
                                placeholder="Vendor name"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Account Name</label>
                        <input
                            type="text"
                            name="accountName"
                            value={formData.accountName}
                            onChange={handleChange}
                            placeholder="Account name"
                        />
                    </div>

                    <div className="form-group">
                        <label>Mail Message ID</label>
                        <input
                            type="text"
                            name="mailMessageId"
                            value={formData.mailMessageId}
                            onChange={handleChange}
                            placeholder="Email message ID"
                        />
                    </div>

                    <div className="form-group">
                        <label>Notes</label>
                        <textarea
                            name="notes"
                            value={formData.notes}
                            onChange={handleChange}
                            rows="3"
                            placeholder="Additional notes"
                        />
                    </div>

                    <div className="modal-actions">
                        <div></div> {/* Empty div for spacing */}
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Create Transaction
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateTransactionModal;