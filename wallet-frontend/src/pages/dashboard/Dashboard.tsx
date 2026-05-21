import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { axiosInstance } from '../../api/axiosInstance';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { useTranslation } from 'react-i18next';

export default function Dashboard() {
    const { t } = useTranslation();
    const [stats, setStats] = useState<any>(null);
    const [accounts, setAccounts] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDashboardData = async () => {
            setLoading(true);
            
            // 1. Спочатку вантажимо рахунки
            try {
                const accountsRes = await axiosInstance.get('/accounts');
                setAccounts(accountsRes.data);
            } catch (error) {
                console.error("Помилка завантаження рахунків", error);
            }

            // 2. Окремо вантажимо статистику, щоб вона не блокувала рахунки
            try {
                const statsRes = await axiosInstance.get('/transactions/statistics');
                setStats(statsRes.data);
            } catch (error) {
                console.error("Помилка завантаження статистики", error);
            }

            setLoading(false);
        };

        fetchDashboardData();
    }, []);

    if (loading) {
        return <div style={{ color: '#fff', padding: '30px' }}>{t('common.loading', 'Завантаження аналітики...')}</div>;
    }

    return (
        <div style={{ color: '#e0e0e0', fontFamily: 'sans-serif', maxWidth: '1200px', margin: '0 auto' }}>
            <h1 style={{ marginBottom: '30px', fontSize: '28px' }}>{t('dashboard.title', 'Огляд гаманця')}</h1>

            {/* Блоки статистики */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '40px' }}>
                <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                    <h3 style={{ margin: '0 0 10px 0', color: '#aaa', fontSize: '16px', fontWeight: 'normal' }}>{t('dashboard.totalBalance', 'Загальний баланс (UAH)')}</h3>
                    <p style={{ margin: 0, fontSize: '32px', fontWeight: 'bold', color: '#fff' }}>{stats?.balance || 0} UAH</p>
                </div>
                <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                    <h3 style={{ margin: '0 0 10px 0', color: '#aaa', fontSize: '16px', fontWeight: 'normal' }}>{t('dashboard.income', 'Доходи')}</h3>
                    <p style={{ margin: 0, fontSize: '32px', fontWeight: 'bold', color: '#81c784' }}>+{stats?.totalIncome || 0} UAH</p>
                </div>
                <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                    <h3 style={{ margin: '0 0 10px 0', color: '#aaa', fontSize: '16px', fontWeight: 'normal' }}>{t('dashboard.expenses', 'Витрати')}</h3>
                    <p style={{ margin: 0, fontSize: '32px', fontWeight: 'bold', color: '#ff8a80' }}>-{stats?.totalExpenses || 0} UAH</p>
                </div>
            </div>

            {/* Блок рахунків */}
            <div style={{ marginBottom: '40px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h2 style={{ margin: 0, color: '#fff', fontSize: '22px' }}>{t('accounts.title', 'Мої рахунки')}</h2>
                    <button onClick={() => navigate('/accounts/new')} style={{ padding: '10px 20px', background: '#4CAF50', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
                        {t('accounts.add', 'Створити рахунок')}
                    </button>
                </div>
                
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                    {accounts.map(acc => (
                        <div key={acc.id} onClick={() => navigate(`/accounts/edit/${acc.id}`)} style={{ background: '#1e1e1e', padding: '20px', borderRadius: '8px', border: '1px solid #333', cursor: 'pointer' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                                <span style={{ fontWeight: 'bold', fontSize: '18px', color: '#fff' }}>{acc.title}</span>
                                <span style={{ fontSize: '12px', background: '#333', padding: '3px 8px', borderRadius: '4px', color: '#aaa' }}>{acc.type}</span>
                            </div>
                            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#2196F3', marginTop: '10px' }}>
                                {acc.balance} {acc.currency}
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Графік витрат */}
            <div style={{ background: '#1e1e1e', padding: '25px', borderRadius: '8px', border: '1px solid #333' }}>
                <h2 style={{ margin: '0 0 20px 0', color: '#fff', fontSize: '22px' }}>{t('dashboard.expense_structure')}</h2>
                <div style={{ height: '350px' }}>
                    {stats?.categories?.length > 0 ? (
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie data={stats.categories} dataKey="amount" nameKey="name" cx="50%" cy="50%" innerRadius={80} outerRadius={120} paddingAngle={5}>
                                    {stats.categories.map((entry: any, index: number) => (
                                        <Cell key={`cell-${index}`} fill={entry.color || '#8884d8'} />
                                    ))}
                                </Pie>
                                <Tooltip formatter={(value: number) => [`${value} UAH`, 'Сума']} contentStyle={{ background: '#333', border: 'none', borderRadius: '8px', color: '#fff' }} />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    ) : (
                        <p style={{ color: '#aaa', textAlign: 'center', marginTop: '100px' }}>{t('dashboard.no_data', 'Даних немає')}</p>
                    )}
                </div>
            </div>
        </div>
    );
}