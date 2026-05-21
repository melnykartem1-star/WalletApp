import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

export default function CreateTransaction() {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [accounts, setAccounts] = useState<any[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [merchants, setMerchants] = useState<any[]>([]);

    const [title, setTitle] = useState('');
    const [type, setType] = useState('WITHDRAW');
    const [amount, setAmount] = useState('');
    const [accountId, setAccountId] = useState('');
    const [categoryId, setCategoryId] = useState('');
    const [merchantId, setMerchantId] = useState('');
    const [description, setDescription] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [accRes, catRes, merRes] = await Promise.all([
                    axiosInstance.get('/accounts'),
                    axiosInstance.get('/categories'),
                    axiosInstance.get('/merchants')
                ]);
                setAccounts(accRes.data);
                setCategories(catRes.data?.content || catRes.data || []);
                setMerchants(merRes.data);
            } catch {
                toast.error(t('common.error_load', 'Помилка завантаження даних'));
            }
        };
        fetchData();
    }, [t]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axiosInstance.post('/transactions', {
                title,
                type,
                amount: parseFloat(amount),
                accountId: parseInt(accountId),
                categoryId: categoryId ? parseInt(categoryId) : null,
                merchantId: merchantId ? parseInt(merchantId) : null,
                description: description || null
            });
            toast.success(t('transactions.create_success', 'Транзакцію створено!'));
            navigate('/transactions');
        } catch (error: any) {
            toast.error(error.response?.data?.message || t('transactions.create_error', 'Помилка при створенні'));
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', width: '100%' }}>
            <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '600px', width: '100%', padding: '20px' }}>
                <h2 style={{ marginBottom: '20px', textAlign: 'center' }}>{t('transactions.new', 'Нова транзакція')}</h2>
                
                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px', background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333', boxShadow: '0 4px 15px rgba(0,0,0,0.3)' }}>
                    
                    <input type="text" placeholder={t('transactions.fields.title', 'Назва транзакції')} value={title} onChange={e => setTitle(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />
                    
                    <select value={type} onChange={e => setType(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="WITHDRAW">{t('transactions.withdraw', 'Витрата')}</option>
                        <option value="DEPOSIT">{t('transactions.deposit', 'Дохід')}</option>
                    </select>

                    <input type="number" step="0.01" placeholder={t('transactions.fields.amount', 'Сума')} value={amount} onChange={e => setAmount(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff' }} />
                    
                    <select value={accountId} onChange={e => setAccountId(e.target.value)} required style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="">{t('transactions.fields.select_account', 'Оберіть рахунок')}</option>
                        {accounts.map(a => <option key={a.id} value={a.id}>{a.title}</option>)}
                    </select>

                    <select value={categoryId} onChange={e => setCategoryId(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="">{t('transactions.fields.select_category', 'Оберіть категорію')}</option>
                        {categories.map(c => <option key={c.id} value={c.id}>{c.title}</option>)}
                    </select>

                    <select value={merchantId} onChange={e => setMerchantId(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', cursor: 'pointer' }}>
                        <option value="">{t('transactions.fields.select_merchant', 'Оберіть продавця')}</option>
                        {merchants.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
                    </select>

                    <textarea placeholder={t('transactions.fields.description', 'Опис')} value={description} onChange={e => setDescription(e.target.value)} style={{ padding: '12px', borderRadius: '6px', border: '1px solid #444', background: '#121212', color: '#fff', minHeight: '80px', resize: 'none' }} />

                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                        <button type="submit" style={{ flex: 1, padding: '12px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>{t('common.create', 'Створити')}</button>
                        <button type="button" onClick={() => navigate('/transactions')} style={{ padding: '12px', background: '#444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}>{t('common.cancel', 'Скасувати')}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}