import React, { useState } from 'react';
import './AdvancedSearch.css';

function AdvancedSearch({ categories, onSearch, onClear }) {
    const [showAdvanced, setShowAdvanced] = useState(false);
    const [searchParams, setSearchParams] = useState({
        description: '',
        category: '',
        vendor: '',
        notes: '',
        startDate: getDefaultStartDate(),
        endDate: ''
    });

    // Helper function to get date 30 days ago
    function getDefaultStartDate() {
        const date = new Date();
        date.setDate(date.getDate() - 30);
        return date.toISOString().split('T')[0]; // Format: YYYY-MM-DD
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setSearchParams(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(searchParams);
    };

    const handleClear = () => {
        const emptyParams = {
            description: '',
            category: '',
            vendor: '',
            notes: '',
            startDate: '',
            endDate: ''
        };
        setSearchParams(emptyParams);
        onClear();
    };

    const hasActiveFilters = Object.values(searchParams).some(val => val !== '');

    return (
        <div className="advanced-search">
            <div className="search-header">
                <button
                    type="button"
                    className="toggle-advanced"
                    onClick={() => setShowAdvanced(!showAdvanced)}
                >
                    <span className="toggle-icon">{showAdvanced ? '▼' : '▶'}</span>
                    <span>Advanced Search</span>
                    {hasActiveFilters && <span className="active-indicator">●</span>}
                </button>
                {hasActiveFilters && (
                    <button type="button" className="clear-filters" onClick={handleClear}>
                        Clear Filters
                    </button>
                )}
            </div>

            {showAdvanced && (
                <form onSubmit={handleSubmit} className="search-form">
                    <div className="search-grid">
                        <div className="search-field">
                            <label>Description</label>
                            <input
                                type="text"
                                name="description"
                                placeholder="Search description..."
                                value={searchParams.description}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="search-field">
                            <label>Category</label>
                            <select
                                name="category"
                                value={searchParams.category}
                                onChange={handleChange}
                            >
                                <option value="">All Categories</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.name}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className="search-field">
                            <label>Vendor</label>
                            <input
                                type="text"
                                name="vendor"
                                placeholder="Search vendor..."
                                value={searchParams.vendor}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="search-field">
                            <label>Notes</label>
                            <input
                                type="text"
                                name="notes"
                                placeholder="Search notes..."
                                value={searchParams.notes}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="search-field">
                            <label>Start Date</label>
                            <input
                                type="date"
                                name="startDate"
                                value={searchParams.startDate}
                                defaultValue="2026-02-01"
                                onChange={handleChange}
                            />
                        </div>

                        <div className="search-field">
                            <label>End Date</label>
                            <input
                                type="date"
                                name="endDate"
                                value={searchParams.endDate}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="search-actions">
                        <button type="submit" className="btn-primary">
                            Search
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}

export default AdvancedSearch;