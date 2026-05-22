import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function Transactions() {
    const { t } = useTranslation();
    const [transactions, setTransactions] = useState<any[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [searchQuery, setSearchQuery] = useState('');
    const [filterType, setFilterType] = useState('');
    const [filterCategoryId, setFilterCategoryId] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [activeFilters, setActiveFilters] = useState({ query: '', type: '', catId: '', start: '', end: '' });

    const navigate = useNavigate();

    useEffect(() => {
        axiosInstance.get('/categories?size=100')
            .then(res => setCategories(res.data.content || []))
            .catch(e => console.error("Помилка завантаження категорій", e));
    }, []);

    const fetchTransactions = useCallback(async (pageNumber: number, filters: any) => {
        setLoading(true);
        setError(null);
        try {
            let url = `/transactions?page=${pageNumber}&size=10`;
            if (filters.query) url += `&query=${encodeURIComponent(filters.query)}`;
            if (filters.type) url += `&type=${filters.type}`;
            if (filters.catId) url += `&categoryId=${filters.catId}`;
            if (filters.start) url += `&startDate=${filters.start}T00:00:00`;
            if (filters.end) url += `&endDate=${filters.end}T23:59:59`;

            const res = await axiosInstance.get(url);
            setTransactions(res.data.content || []);
            setTotalPages(res.data.totalPages);
        } catch (error) {
            console.error("Помилка завантаження транзакцій", error);
            setError(t('transactions.error_load', 'Не вдалося завантажити транзакції.'));
        } finally {
            setLoading(false);
        }
    }, [t]);

    useEffect(() => {
        fetchTransactions(page, activeFilters);
    }, [page, activeFilters, fetchTransactions]);

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        setActiveFilters({ query: searchQuery, type: filterType, catId: filterCategoryId, start: startDate, end: endDate });
        setPage(0);
    };

    const handleResetFilters = () => {
        setSearchQuery(''); setFilterType(''); setFilterCategoryId(''); setStartDate(''); setEndDate('');
        setActiveFilters({ query: '', type: '', catId: '', start: '', end: '' });
        setPage(0);
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm(t('transactions.delete_confirm'))) return;
        try {
            await axiosInstance.delete(`/transactions/${id}`);
            fetchTransactions(page, activeFilters);
            toast.success(t('transactions.delete_success', 'Видалено'));
        } catch {
            toast.error(t('transactions.delete_error', 'Помилка при видаленні'));
        }
    };

    const exportToCSV = () => {
        if (transactions.length === 0) {
            alert(t('transactions.export_empty'));
            return;
        }

        const headers = [
            t('transactions.table.date'), 
            t('transactions.table.title'), 
            t('transactions.table.description', 'Опис'),
            t('transactions.table.category_merchant'), 
            'Тип', 
            t('transactions.table.amount')
        ];
        
        const csvRows = transactions.map(tx => {
            const date = new Date(tx.createdAt).toLocaleDateString('uk-UA');
            const title = `"${tx.title.replace(/"/g, '""')}"`;
            const description = tx.description ? `"${tx.description.replace(/"/g, '""')}"` : '""';
            const category = tx.category ? tx.category.title : t('transactions.no_data');
            const merchant = tx.merchant ? tx.merchant.name : t('transactions.no_data');
            const typeLabel = tx.type === 'WITHDRAW' ? t('transactions.withdraw') : tx.type === 'DEPOSIT' ? t('transactions.deposit') : t('transactions.transfer_type');
            
            // Додано відображення валюти в експорті
            const currency = tx.currency || tx.account?.currency || '';
            const amount = (tx.type === 'WITHDRAW' ? `-${tx.amount}` : tx.amount) + (currency ? ` ${currency}` : '');
            
            return [date, title, description, `${category} / ${merchant}`, typeLabel, amount].join(';');
        });

        const csvContent = '\uFEFF' + [headers.join(';'), ...csvRows].join('\n');
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', `transactions_${new Date().toLocaleDateString('uk-UA')}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h1 style={{ margin: 0, fontSize: '28px' }}>{t('transactions.title')}</h1>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={exportToCSV} style={{ padding: '10px 15px', background: '#f57c00', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('transactions.export')}</button>
                    <button onClick={() => navigate('/transactions/transfer')} style={{ padding: '10px 15px', background: '#1976d2', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('transactions.transfer')}</button>
                    <button onClick={() => navigate('/transactions/new')} style={{ padding: '10px 15px', background: '#2e7d32', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('transactions.new')}</button>
                </div>
            </div>

            <div style={{ background: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333', marginBottom: '20px' }}>
                <form onSubmit={handleSearch} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <div style={{ display: 'flex', gap: '15px' }}>
                        <input type="text" placeholder={t('transactions.search_placeholder')} value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} style={{ flex: 1, padding: '12px 15px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', fontSize: '15px' }} />
                        <button type="submit" style={{ padding: '0 25px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('transactions.find')}</button>
                        <button type="button" onClick={handleResetFilters} style={{ padding: '0 20px', background: '#d32f2f', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('transactions.reset')}</button>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '15px' }}>
                        <select value={filterType} onChange={e => setFilterType(e.target.value)} style={{ padding: '10px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }}>
                            <option value="">{t('transactions.all_types')}</option>
                            <option value="WITHDRAW">{t('transactions.withdraw')}</option>
                            <option value="DEPOSIT">{t('transactions.deposit')}</option>
                            <option value="TRANSFER">{t('transactions.transfer_type')}</option>
                        </select>
                        <select value={filterCategoryId} onChange={e => setFilterCategoryId(e.target.value)} style={{ padding: '10px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }}>
                            <option value="">{t('transactions.all_categories')}</option>
                            {categories.map(c => <option key={c.id} value={c.id}>{c.title}</option>)}
                        </select>
                        <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} style={{ padding: '10px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', colorScheme: 'dark' }} />
                        <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} style={{ padding: '10px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', colorScheme: 'dark' }} />
                    </div>
                </form>
            </div>

            {loading ? (
                <p style={{ textAlign: 'center', padding: '20px' }}>{t('transactions.loading')}</p>
            ) : error ? (
                <div style={{ textAlign: 'center', padding: '40px', background: '#1e1e1e', borderRadius: '8px', border: '1px solid #ef5350' }}>
                    <p style={{ color: '#ef5350', marginBottom: '20px' }}>{error}</p>
                    <button onClick={() => fetchTransactions(page, activeFilters)} style={{ padding: '10px 20px', background: '#ef5350', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>
                        {t('common.retry', 'Спробувати ще раз')}
                    </button>
                </div>
            ) : (
                <div style={{ background: '#1e1e1e', borderRadius: '8px', border: '1px solid #333', overflow: 'hidden' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                        <thead style={{ background: '#2d2d2d', borderBottom: '2px solid #333' }}>
                            <tr>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal' }}>{t('transactions.table.date')}</th>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal' }}>{t('transactions.table.title')}</th>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal' }}>{t('transactions.table.description', 'Опис')}</th>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal' }}>{t('transactions.table.category_merchant')}</th>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal', textAlign: 'right' }}>{t('transactions.table.amount')}</th>
                                <th style={{ padding: '15px', color: '#aaa', fontWeight: 'normal', textAlign: 'center' }}>{t('transactions.table.actions')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {transactions.length === 0 ? (
                                <tr><td colSpan={6} style={{ padding: '30px', textAlign: 'center', color: '#666' }}>{t('transactions.no_transactions')}</td></tr>
                            ) : transactions.map(tx => (
                                <tr key={tx.id} style={{ borderBottom: '1px solid #333' }}>
                                    <td style={{ padding: '15px', color: '#aaa', fontSize: '14px' }}>{new Date(tx.createdAt).toLocaleDateString('uk-UA')}</td>
                                    <td style={{ padding: '15px', fontWeight: '500' }}>{tx.title}</td>
                                    <td style={{ padding: '15px', color: '#aaa', fontSize: '13px', maxWidth: '250px', wordWrap: 'break-word', whiteSpace: 'normal' }}>
                                        {tx.description || '-'}
                                    </td>
                                    <td style={{ padding: '15px' }}>
                                        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                                            {tx.category && <span style={{ background: '#333', padding: '4px 10px', borderRadius: '4px', fontSize: '12px' }}>{tx.category.title}</span>}
                                            {tx.merchant && <span style={{ border: '1px solid #444', padding: '3px 10px', borderRadius: '4px', fontSize: '12px' }}>{tx.merchant.name}</span>}
                                        </div>
                                    </td>
                                    <td style={{ padding: '15px', textAlign: 'right', fontWeight: 'bold', fontSize: '16px', color: tx.type === 'WITHDRAW' ? '#ff8a80' : tx.type === 'DEPOSIT' ? '#81c784' : '#64b5f6' }}>
                                        {/* Додано відображення валюти в таблиці */}
                                        {tx.type === 'WITHDRAW' ? '-' : tx.type === 'DEPOSIT' ? '+' : '⇄'}
                                        {tx.amount} {tx.currency || tx.account?.currency || ''}
                                    </td>
                                    <td style={{ padding: '15px', textAlign: 'center' }}>
                                        <button onClick={() => handleDelete(tx.id)} style={{ background: 'transparent', color: '#ef5350', border: '1px solid #ef5350', padding: '6px 12px', borderRadius: '4px', cursor: 'pointer' }}>{t('transactions.table.actions')}</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '15px', marginTop: '20px', alignItems: 'center' }}>
                <button disabled={page === 0} onClick={() => setPage(p => p - 1)} style={{ padding: '8px 15px', background: page === 0 ? '#222' : '#333', color: page === 0 ? '#555' : '#fff', border: 'none', borderRadius: '4px', cursor: page === 0 ? 'default' : 'pointer' }}>{t('transactions.prev')}</button>
                <span style={{ color: '#aaa' }}>{t('transactions.page_info', { current: page + 1, total: totalPages || 1 })}</span>
                <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)} style={{ padding: '8px 15px', background: page >= totalPages - 1 ? '#222' : '#333', color: page >= totalPages - 1 ? '#555' : '#fff', border: 'none', borderRadius: '4px', cursor: page >= totalPages - 1 ? 'default' : 'pointer' }}>{t('transactions.next')}</button>
            </div>
        </div>
    );
}