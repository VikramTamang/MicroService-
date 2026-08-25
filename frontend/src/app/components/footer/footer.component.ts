import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer class="border-t border-slate-800/80 bg-slate-950/60 mt-20">
      <div class="max-w-7xl mx-auto px-4 py-12 sm:px-6 lg:px-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div class="space-y-3">
            <span class="text-lg font-bold font-['Outfit'] bg-gradient-to-r from-white via-slate-100 to-emerald-400 bg-clip-text text-transparent">ApexStore</span>
            <p class="text-xs text-slate-400 leading-relaxed">
              Enterprise microservices architecture showcase built with Spring Boot 3, Spring Cloud Gateway, Eureka, OpenFeign, Resilience4j, and Angular 18+.
            </p>
          </div>
          <div>
            <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-300 mb-3">Microservices Stack</h4>
            <ul class="text-xs text-slate-400 space-y-2">
              <li class="flex items-center space-x-2"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>Eureka Discovery (:8761)</span></li>
              <li class="flex items-center space-x-2"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>API Gateway (:8080)</span></li>
              <li class="flex items-center space-x-2"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>User / Auth Service (:8081)</span></li>
              <li class="flex items-center space-x-2"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>Product / Catalog Service (:8082)</span></li>
              <li class="flex items-center space-x-2"><span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>Order Service (:8083)</span></li>
            </ul>
          </div>
          <div>
            <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-300 mb-3">Resilience & Gateway</h4>
            <ul class="text-xs text-slate-400 space-y-2">
              <li>OpenFeign Inter-service RPC</li>
              <li>Resilience4j Circuit Breakers</li>
              <li>Stateless JWT Authentication</li>
              <li>Isolated DBs + Flyway Migrations</li>
            </ul>
          </div>
          <div>
            <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-300 mb-3">Status</h4>
            <div class="p-3 rounded-xl bg-slate-900/80 border border-slate-800 space-y-2">
              <div class="flex items-center space-x-2">
                <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                <span class="text-xs font-medium text-slate-200">Gateway Active</span>
              </div>
              <p class="text-[11px] text-slate-400">All client traffic routes securely through port 8080.</p>
            </div>
          </div>
        </div>
        <div class="border-t border-slate-800/60 mt-8 pt-8 flex flex-col sm:flex-row justify-between items-center text-xs text-slate-500">
          <p>© 2026 ApexStore Microservices Platform. All rights reserved.</p>
          <p class="mt-2 sm:mt-0">Powered by Spring Cloud & Angular 18+</p>
        </div>
      </div>
    </footer>
  `
})
export class FooterComponent {}
