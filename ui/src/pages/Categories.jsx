import React, { useState, useEffect } from 'react';

function Categories() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            const response = await fetch(`${process.env.REACT_APP_API_URL}/api/categories/`);
            if (!response.ok) throw new Error('Failed to fetch categories');
            const data = await response.json();
            setCategories(Array.isArray(data) ? data : []);
        } catch (err) {
            // Mock data for demonstration
            setCategories([
                { id: 1, name: 'Groceries', type: 'Expense', count: 45 },
                { id: 2, name: 'Income', type: 'Income', count: 12 },
                { id: 3, name: 'Transportation', type: 'Expense', count: 28 },
                { id: 4, name: 'Entertainment', type: 'Expense', count: 15 },
                { id: 5, name: 'Utilities', type: 'Expense', count: 8 },
                { id: 6, name: 'Dining', type: 'Expense', count: 32 }
            ]);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="page-container">
                <div className="loading-container">
                    <div className="loading-spinner"></div>
                    <div className="loading-text">Loading categories...</div>
                </div>
            </div>
        );
    }

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">Categories</h1>
                <p className="page-description">Organize your transactions by category</p>
            </div>

            <div className="table-container">
                <div className="table-wrapper">
                    <table className="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Category Name</th>
                            {/*<th>Type</th>*/}
                            {/*<th className="text-right">Transaction Count</th>*/}
                        </tr>
                        </thead>
                        <tbody>
                        {categories.map((category) => (
                            <tr key={category.id}>
                                <td className="font-medium">#{category.id}</td>
                                <td>{category.name}</td>
                                {/*<td>*/}
                                {/*<span className={`type-badge ${category.type.toLowerCase()}`}>*/}
                                {/*  {category.type}*/}
                                {/*</span>*/}
                                {/*</td>*/}
                                {/*<td className="text-right">{category.count}</td>*/}
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}

export default Categories;