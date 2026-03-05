import React, { useState, useEffect } from 'react';
import './TransactionModal.css';

function TransactionModal({ transaction, categories, onClose, onSave, onDelete }) {
    const [formData, setFormData] = useState({
        date: '',
        description: '',
        originalDescription: '',
        amount: '',
        type: '',
        category: '',
        vendor: '',
        accountName: '',
        mailId: '',
        notes: ''
    });

    useEffect(() => {
        if (transaction) {
            let formattedDate = '';
            if (transaction.date) {
                // Parse the date correctly (not as UTC)
                const date = new Date(transaction.date.replace(' ', 'T'));

                // Format for datetime-local input (YYYY-MM-DDTHH:mm:ss)
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                const hours = String(date.getHours()).padStart(2, '0');
                const minutes = String(date.getMinutes()).padStart(2, '0');
                const seconds = String(date.getSeconds()).padStart(2, '0');

                formattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
            }

            setFormData({
                date: formattedDate, // Extract just the date part
                description: transaction.description || '',
                originalDescription: transaction.originalDescription || '',
                amount: transaction.amount || '',
                type: transaction.type || '',
                category: transaction.category || '',
                vendor: transaction.vendor || '',
                accountName: transaction.accountName || '',
                mailId: transaction.mailId || '',
                notes: transaction.notes || ''
            });
        }
    }, [transaction]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        await onSave(transaction.id, formData);
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this transaction? This action cannot be undone.')) {
            await onDelete(transaction.id);
        }
    };

    if (!transaction) return null;

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Edit Transaction</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-row">
                        <div className="form-group">
                            <label>Transaction ID</label>
                            <input type="text" value={transaction.id} disabled />
                        </div>

                        <div className="form-group">
                            <label>Date</label>
                            <input
                                type="datetime-local"
                                name="date"
                                value={formData.date}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Description</label>
                        <input
                            type="text"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Original Description</label>
                        <input
                            type="text"
                            name="originalDescription"
                            value={formData.originalDescription}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Amount</label>
                            <input
                                type="number"
                                step="0.01"
                                name="amount"
                                value={formData.amount}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Type</label>
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
                        />
                    </div>

                    <div className="form-group">
                        <label>Notes</label>
                        <textarea
                            name="notes"
                            value={formData.notes}
                            onChange={handleChange}
                            rows="3"
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
                        <button type="button" onClick={onClose} className="btn-secondary">
                            Cancel
                        </button>
                        <button type="submit" className="btn-primary">
                            Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default TransactionModal;