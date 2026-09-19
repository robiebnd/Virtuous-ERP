"use client";

import { useEffect, useMemo, useState } from "react";
import { productsApi, productMasterDataApi } from "@/lib/api";
import { ModuleWorkspace } from "@/components/ModuleWorkspace";

export default function ProductsPage() {
  const [products,setProducts]=useState<any[]>([]);
  const [categories,setCategories]=useState<any[]>([]);
  const [units,setUnits]=useState<any[]>([]);
  const [search,setSearch]=useState("");
  const [loading,setLoading]=useState(true);
  const [error,setError]=useState("");
  const [open,setOpen]=useState(false);
  const [editing,setEditing]=useState<any|null>(null);
  const [form,setForm]=useState<any>({sku:"",name:"",description:"",costPrice:"",sellingPrice:"",categoryId:"",unitOfMeasureId:"",active:true});

  const load=async()=>{setLoading(true);setError("");try{const [p,c,u]=await Promise.all([productsApi.list(),productMasterDataApi.categories(),productMasterDataApi.units()]);setProducts(p);setCategories(c);setUnits(u);}catch(e:any){setError(e.message||"Unable to load products.");}finally{setLoading(false);}};
  useEffect(()=>{load();},[]);

  const filtered=useMemo(()=>products.filter(p=>[p.sku,p.name,p.description,p.categoryName,p.unitName].join(" ").toLowerCase().includes(search.toLowerCase())),[products,search]);

  const startCreate=()=>{setEditing(null);setForm({sku:"",name:"",description:"",costPrice:"",sellingPrice:"",categoryId:"",unitOfMeasureId:"",active:true});setOpen(true);};
  const startEdit=(p:any)=>{setEditing(p);setForm({sku:p.sku,name:p.name,description:p.description||"",costPrice:p.costPrice??"",sellingPrice:p.sellingPrice??"",categoryId:p.categoryId||"",unitOfMeasureId:p.unitOfMeasureId||"",active:p.active!==false});setOpen(true);};
  const save=async(e:React.FormEvent)=>{e.preventDefault();setError("");try{const body={...form,costPrice:Number(form.costPrice),sellingPrice:Number(form.sellingPrice),categoryId:form.categoryId,unitOfMeasureId:form.unitOfMeasureId};if(editing)await productsApi.update(editing.id,body);else await productsApi.create(body);setOpen(false);await load();}catch(e:any){setError(e.message||"Unable to save product.");}};

  const rows=filtered.map(p=>({id:p.id,SKU:p.sku,Product:p.name,Category:p.categoryName||"—","UOM":p.unitName||"—","Cost Price":Number(p.costPrice||0).toFixed(2),"Selling Price":Number(p.sellingPrice||0).toFixed(2),Status:p.active===false?"INACTIVE":"ACTIVE"}));

  return <main className="content">
    <div className="page-head"><div><div className="eyebrow">WAREHOUSE / MASTER DATA</div><h1>Products</h1><p>Maintain the product master used by procurement, inventory, warehouse and outbound operations.</p></div><button className="btn primary" onClick={startCreate}>Create Product</button></div>
    {error&&<div className="alert error">{error}</div>}
    <div className="toolbar"><input className="filter" placeholder="Search SKU, product, category..." value={search} onChange={e=>setSearch(e.target.value)}/><span className="section-meta">{filtered.length} of {products.length} products</span></div>
    {loading?<div className="card empty">Loading products…</div>:<ModuleWorkspace title="Product Master" subtitle={`${filtered.length} products`} columns={[{key:"SKU",label:"SKU"},{key:"Product",label:"Product"},{key:"Category",label:"Category"},{key:"UOM",label:"UOM"},{key:"Cost Price",label:"Cost"},{key:"Selling Price",label:"Selling Price"},{key:"Status",label:"Status"}]} rows={rows}>{null}</ModuleWorkspace>}
    {filtered.length>0&&<div className="card" style={{marginTop:18,padding:16}}><div className="section-title">Product Actions</div><div className="product-action-list">{filtered.slice(0,20).map(p=><div className="product-action-row" key={p.id}><div><b>{p.sku}</b><span>{p.name}</span></div><button className="btn" onClick={()=>startEdit(p)}>Edit</button></div>)}</div></div>}
    {open&&<div className="modal-backdrop" onMouseDown={e=>e.currentTarget===e.target&&setOpen(false)}><form className="modal-card" onSubmit={save}><div className="modal-head"><div><div className="eyebrow">{editing?"PRODUCT / EDIT":"PRODUCT / CREATE"}</div><h2>{editing?"Edit Product":"Create Product"}</h2></div><button type="button" className="icon-btn" onClick={()=>setOpen(false)}>×</button></div><div className="form-grid">
      <label className="form-field"><span>SKU</span><input className="form-input" value={form.sku} onChange={e=>setForm({...form,sku:e.target.value})} placeholder="Optional — system can assign"/></label>
      <label className="form-field"><span>Product Name</span><input className="form-input" required value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></label>
      <label className="form-field"><span>Category</span><select className="form-input" required value={form.categoryId} onChange={e=>setForm({...form,categoryId:e.target.value})}><option value="">Select category</option>{categories.map(c=><option key={c.id} value={c.id}>{c.code} — {c.name}</option>)}</select></label>
      <label className="form-field"><span>Unit of Measure</span><select className="form-input" required value={form.unitOfMeasureId} onChange={e=>setForm({...form,unitOfMeasureId:e.target.value})}><option value="">Select UOM</option>{units.map(u=><option key={u.id} value={u.id}>{u.code} — {u.name}</option>)}</select></label>
      <label className="form-field"><span>Cost Price</span><input className="form-input" required type="number" min="0" step="0.01" value={form.costPrice} onChange={e=>setForm({...form,costPrice:e.target.value})}/></label>
      <label className="form-field"><span>Selling Price</span><input className="form-input" required type="number" min="0" step="0.01" value={form.sellingPrice} onChange={e=>setForm({...form,sellingPrice:e.target.value})}/></label>
      <label className="form-field" style={{gridColumn:"1 / -1"}}><span>Description</span><textarea className="form-input" style={{height:90,paddingTop:10}} value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label>
      <label className="form-field"><span>Status</span><select className="form-input" value={form.active?"true":"false"} onChange={e=>setForm({...form,active:e.target.value==="true"})}><option value="true">Active</option><option value="false">Inactive</option></select></label>
    </div><div className="modal-actions"><button type="button" className="btn" onClick={()=>setOpen(false)}>Cancel</button><button className="btn primary" type="submit">{editing?"Save Changes":"Create Product"}</button></div></form></div>}
  </main>;
}
