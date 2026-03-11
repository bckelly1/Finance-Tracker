import React, { useState } from 'react';
import './TransactionModal.css';

function CreateBudgetModal({ categories, accounts, onClose, onCreate }) {
    const [formData, setFormData] = useState({
        name: '',
        limitAmount: '',
        period: 'MONTHLY',
        categoryId: '',
        accountId: '',
        startDate: new Date().toISOString().split('T')[0]
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

        // Convert to numbers where needed
        const dataToSend = {
            ...formData,
            limitAmount: parseFloat(formData.limitAmount),
            categoryId: formData.categoryId ? parseInt(formData.categoryId) : null,
            accountId: formData.accountId ? parseInt(formData.accountId) : null
        };

        await onCreate(dataToSend);
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                <div className="modal-header">
                    <h2>Create New Budget</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <form onSubmit={handleSubmit} className="modal-form">
                    <div className="form-group">
                        <label>Budget Name *</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            placeholder="e.g., Monthly Groceries"
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Limit Amount *</label>
                            <input
                                type="number"
                                step="0.01"
                                name="limitAmount"
                                value={formData.limitAmount}
                                onChange={handleChange}
                                required
                                placeholder="0.00"
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

                    <div className="form-row">
                        <div className="form-group">
                            <label>Category</label>
                            <select
                                name="categoryId"
                                value={formData.categoryId}
                                onChange={handleChange}
                            >
                                <option value="">Select Category</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Account</label>
                            <select
                                name="accountId"
                                value={formData.accountId}
                                onChange={handleChange}
                            >
                                <option value="">Select Account</option>
                                {accounts.map(acc => (
                                    <option key={acc.id} value={acc.id}>{acc.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Start Date *</label>
                        <input
                            type="date"
                            name="startDate"
                            value={formData.startDate}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="modal-actions">
                        <div></div>
                        <div className="modal-actions-right">
                            <button type="button" onClick={onClose} className="btn-secondary">
                                Cancel
                            </button>
                            <button type="submit" className="btn-primary">
                                Create Budget
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateBudgetModal;